package com.zhangshuo.zvcompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

final class ProxyInfo {
    private static final String PROXY = "Proxy";

    private String packageName = "";//包名
    private String targetClassName = "";//目标类名
    private String proxyClassName = "";//代理类名

    private Map<String, ViewInfo> idViewMap = new LinkedHashMap<>();

    public ProxyInfo(String packageName, String targetClassName) {
        this.packageName = packageName;
        this.targetClassName = targetClassName;
        this.proxyClassName = targetClassName + "$$" + PROXY;
    }

    /**
     * 添加View信息
     *
     * @param id       viewId
     * @param viewInfo viewInfo
     */
    void putViewInfo(String id, ViewInfo viewInfo) {
        idViewMap.put(id, viewInfo);
    }

    /**
     * @return 目标类名
     */
    private String getTargetClassName() {
        return targetClassName.replace("$", ".");
    }

    void generateJavaCode(ProcessingEnvironment processingEnv) throws IOException {
        final ClassName FINDER_STRATEGY =
                ClassName.get("com.zhangshuo.zvapi", "FindStrategy");
        final ClassName ABSTRACT_INJECT =
                ClassName.get("com.zhangshuo.zvapi", "AbstractInjector");
        final ClassName MODULE_R=ClassName.get(packageName,"R");

        final TypeName T = TypeVariableName.get("T");

        //生成方法
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(FINDER_STRATEGY, "finder", Modifier.FINAL)
                .addParameter(T, "target", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "source",Modifier.FINAL);

        for (Map.Entry<String, ViewInfo> viewInfoEntry : idViewMap.entrySet()) {
            ViewInfo info = viewInfoEntry.getValue();

            builder.addStatement("target.$L = finder.findViewById(source,$T$L)",
                    info.getName(), MODULE_R,info.getId().substring(1));
            //如果是点击事件，则增加点击事件
            String clickEvnentName = info.getClickEventName();
            if ("".equals(clickEvnentName) || clickEvnentName.length() == 0) continue;
            ClassName androidView = ClassName.get("android.view","View");
            builder.addStatement("target.$L.setOnClickListener(new $T.OnClickListener()" +
                    " {" +
                    "@Override " +
                    "public void onClick($T v){ " +
                    "(($T)source).$N " +
                    "}" +
                    "});", info.getName(), androidView,androidView,T,clickEvnentName + "();");

        }
        MethodSpec inject = builder.build();
        //生成类
        String className = proxyClassName;
        TypeSpec proxyClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.FINAL)
                .addTypeVariable(TypeVariableName.get("T extends " + getTargetClassName()))
                .addSuperinterface(ParameterizedTypeName.get(ABSTRACT_INJECT, T))
                .addMethod(inject)
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, proxyClass).build();
        //生成类文件
        javaFile.writeTo(processingEnv.getFiler());

    }

}
