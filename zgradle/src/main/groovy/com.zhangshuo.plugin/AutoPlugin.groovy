import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class AutoPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
       project.afterEvaluate {
           project.getTasks().all { tsk ->
               if (tsk.name.endsWith("Resources")
                       && tsk.name.startsWith("process")
                       && !tsk.name.contains('AndroidTest')) {
                   def buildType = tsk.name.replace("process", "").replace("Resources", "")
                   def taskM = project.task("build" + buildType + "M", dependsOn: tsk) {}
                   tsk.outputs.files.each {
                       if (it.absolutePath.contains('generated/source/r')) {
                           taskM.outputs.file(it.absolutePath)
                       }
                       if (it.absolutePath.contains('intermediates/symbols')
                               ||  it.absolutePath.contains('intermediates/bundles/')) {
                           taskM.inputs.file(it.absolutePath)
                       }
                   }
                   taskM.doLast {
                       autoGenerateR(project, taskM)
                   }
                   tsk.doLast {
                       autoGenerateR(project, taskM)
                   }
               }
           }
       }
    }

    void autoGenerateR(Project project, Task task){
        File inputR = task.inputs.files.files.toArray()[0]
        File outDir = task.outputs.files.files.toArray()[0]
        def manifestFile = project.android.sourceSets.main.manifest.srcFile
        def packageName = new XmlParser().parse(manifestFile).attribute('package')
//    File file = new File(inputR, 'R.txt')
        File file=inputR;
        StringBuffer stringBuffer = new StringBuffer()
        HashMap<String, List> fieldHash = new HashMap<>()
        file.readLines().each {
            String[] fields = it.split(' ')
            if (fields.length == 4) {
                List tmpList = fieldHash.get(fields[1])
                if (tmpList == null) {
                    tmpList = new ArrayList();
                }
                if (fields[1].equals('id')) {
                    tmpList.add('public static final ' + fields[0] + ' ' + fields[2] + ' = ' + fields[3] + ' ;')
                    fieldHash.put(fields[1], tmpList)
                }
            }
        }
        stringBuffer.append('package ' + packageName + ';\n')
        stringBuffer.append('public final class M { \n')
        fieldHash.each { M, v ->
            stringBuffer.append('    public static final class ' + M + ' { \n')
            v.each {
                stringBuffer.append('       ' + it + '\n')
            }
            stringBuffer.append('    }\n')
        }
        stringBuffer.append('}\n')
        File destFile = new File(outDir, '/' + packageName.toString().replace('.', '/') + '/M.java')
        if (!destFile.parentFile.exists()) {
            destFile.parentFile.mkdirs()
        }
        destFile.write(stringBuffer.toString(), 'utf-8')
    }
}