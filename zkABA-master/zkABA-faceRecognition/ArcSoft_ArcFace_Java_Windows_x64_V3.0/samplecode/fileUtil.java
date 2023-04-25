
import java.io.File;
import java.io.FilenameFilter;

/**
 *      文件批处理工具包
 * @author shixingzhou
 * @school 东北大学
 * @creat 2023.02.09
 * @since 1.0.0
 */
public class fileUtil {
    /**
     *  设置想要获取文件类型（即设置需要的文件后缀）
     */
    static class fileAccept implements FilenameFilter{
        String str;
        fileAccept(String s){
            str = "." + s;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(str);
        }
    }

    /**
     *      获取所给目录下指定文件类型的文件
     * @param filePath
     * @param fileType
     * @return
     */
    public File[] getFile(String filePath,String fileType){
        //1.创建File对象，获取图片文件所在目录
        File dir = new File(filePath);

        //2.获取目录下的图片文件
        //2.1 匹配后缀为.###的文件
        fileAccept accept = new fileAccept(fileType);
        //2.2 获取匹配的文件，返回数组中
        File[] fileNames = dir.listFiles(accept);
        return fileNames;
    }

    /**
     *      批量重命名文件
     * @param files
     * @param newFilePath
     * @param newName
     * @param fileType
     */
    public void multiplyRenameFile(File[] files, String newFilePath, String newName, String fileType){
        if (files != null) {
            for (int i=0;i<files.length;i++) {
                //需要改的名字文件
                File toBeRenamed = new File(String.valueOf(files[i]));

                //检查要重命名的文件是否存在
                if (!toBeRenamed.exists()) {
                    System.out.println("File not exists? "+!toBeRenamed.exists());
                    return;
                }
                //需要把文件修改成什么名字 以及存放路径
                File newFile = new File(newFilePath +newName+i+"."+fileType);
                //修改文件名 这个方法会把原来的文件删除掉，但是改完名字的文件内容是不会变得
                if (toBeRenamed.renameTo(newFile)) {
                    System.out.println("Files renamed success!");
                } else {
                    System.out.println("Error renaming files!\n" + "index:" + i);
                }

            }
        }
    }

    /**
     *   测试
     * @param args
     */
    public static void main(String[] args) {
        fileUtil f = new fileUtil();
        String filePath = "F:\\pic2";
        String newFilePath = "F:\\pic2\\";//注意这个路径最后的\\不可省，否则重命名的文件将会被移动到上一层目录
        String fileType = "bmp";
        String newNames = "";
        File[] fileNames = f.getFile(filePath,fileType);
        f.multiplyRenameFile(fileNames,newFilePath,newNames,fileType);
    }
}