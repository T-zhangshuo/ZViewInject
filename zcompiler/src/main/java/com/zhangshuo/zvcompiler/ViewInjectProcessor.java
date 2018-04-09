package com.zhangshuo.zvcompiler;

import com.google.auto.service.AutoService;
import com.zhangshuo.zvanno.ViewInject;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
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

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ViewInject.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtils = processingEnv.getElementUtils();
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
            int id = variableElement.getAnnotation(ViewInject.class).value();
            String clickEvnent = variableElement.getAnnotation(ViewInject.class).clickEvent();
            String fieldName = variableElement.getSimpleName().toString();
            String fieldType = variableElement.asType().toString();

            //打印日志
            String logmsg="annotated pkgName" + packageName + " field : fieldName =" + variableElement.getSimpleName().toString()
                    + ", id =" + id + " , clickEvent = " + clickEvnent + " , fileType = " + fieldType;
            printNote(logmsg);
            //寻找已经存在的类型
            ProxyInfo proxyInfo = proxyInfoMap.get(kClassName);
            //如果是新类型
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(packageName, classname);
                proxyInfoMap.put(kClassName, proxyInfo);
            }
            proxyInfo.putViewInfo(id, new ViewInfo(id, fieldName, clickEvnent));
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

}
