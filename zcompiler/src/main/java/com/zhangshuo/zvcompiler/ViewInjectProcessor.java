package com.zhangshuo.zvcompiler;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.zhangshuo.zvanno.ViewInject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by zhangshuo on 2018/3/20.
 */
@AutoService(Processor.class)
public class ViewInjectProcessor extends AbstractProcessor {
    private Map<String, ProxyInfo> proxyInfoMap = new LinkedHashMap<>();
    private Elements elementsUtils;
    private Trees jcTree;


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ViewInject.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtils = processingEnv.getElementUtils();
        jcTree= Trees.instance(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //遍历所有的类元素
        for (Element e : roundEnvironment.getElementsAnnotatedWith(ViewInject.class)) {
            //只处理成员变量
            if (e.getKind() != ElementKind.FIELD) {
                continue;
            }

            VariableElement variableElement = (VariableElement) e;
            TypeElement typeElement = (TypeElement) e.getEnclosingElement();
            PackageElement packageElement = elementsUtils.getPackageOf(typeElement);

            String kClassName = typeElement.getQualifiedName().toString();
            String packageName = packageElement.getQualifiedName().toString();
            String classname = getClassNameFromType(typeElement, packageName);

            /*对应View信息*/
            ViewInject annotation = variableElement.getAnnotation(ViewInject.class);
            //反射得到view名称
            int viewId = annotation.value();
            Id id=getViewName(ViewInject.class,e,viewId);
            String code=id.code.toString();
            String resourceStr=code.substring(code.indexOf("R.id"));
            String clickEvnent = annotation.clickEvent();
            String fieldName = variableElement.getSimpleName().toString();
            String fieldType = variableElement.asType().toString();

            //打印日志
            String logmsg = "annotated pkgName" + packageName + " field : fieldName =" + variableElement.getSimpleName().toString()
                    + ", id =" + viewId + " , clickEvent = " + clickEvnent + " , fileType = " + fieldType + ", resourceId = " +resourceStr ;
            printNote(logmsg);
            //寻找已经存在的类型
            ProxyInfo proxyInfo = proxyInfoMap.get(kClassName);
            //如果是新类型
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(packageName, classname);
                proxyInfoMap.put(kClassName, proxyInfo);
            }
            proxyInfo.putViewInfo(resourceStr, new ViewInfo(resourceStr, fieldName, clickEvnent));
        }
        //生成对应的代理类
        for (Map.Entry<String, ProxyInfo> proxyInfoEntry : proxyInfoMap.entrySet()) {
            ProxyInfo info = proxyInfoEntry.getValue();
            try {
                info.generateJavaCode(processingEnv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /* 从TypeElement获取包装类型 */
    private static String getClassNameFromType(TypeElement element, String packageName) {
        int packageLen = packageName.length() + 1;
        return element.getQualifiedName().toString()
                .substring(packageLen).replace('.', '$');
    }

    /* 输出信息 */
    private void printNote(String note) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, note);
    }

    private final RScanner rScanner = new RScanner();

    private Id getViewName(Class<? extends Annotation> annotation,Element element,int value){
        AnnotationMirror mirror = getMirror(element, annotation);
        JCTree tree= (JCTree) jcTree.getTree(element,mirror);
        if(tree!=null){
            tree.accept(rScanner);
            return new Id(value,rScanner.rSymbol);
        }
        return new Id(value);
    }

    private static AnnotationMirror getMirror(Element element,
                                              Class<? extends Annotation> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }

    private static class RScanner extends TreeScanner {
        Symbol rSymbol;

        @Override public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            Symbol symbol = jcFieldAccess.sym;
            if (symbol != null
                    && symbol.getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
                rSymbol = symbol;
            } else {
                rSymbol = null;
            }
        }
    }
}
