import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;


public class FaceEngineTest3 {



    public static ArrayList<File> getFiles(String path) throws Exception {
//目标集合fileList
ArrayList<File> fileList = new ArrayList<File>();
File file = new File(path);
if(file.isDirectory()){
File []files = file.listFiles();
for(File fileIndex:files){
//如果这个文件是目录，则进行递归搜索
	if(fileIndex.isDirectory()){
	getFiles(fileIndex.getPath());
	}else {
//如果文件是普通文件，则将文件句柄放入集合中
	fileList.add(fileIndex);
	}
}
        }
return fileList;
    }




    public static void main(String[] args) throws Exception {

        //从官网获取
        String appId = "AxEcLVcwopGAMyvWmA7fVqB5euPJFPFZXTMmnPE9ypza";
        String sdkKey = "6mfXQmf9Ka4z6zzFTzD3QsZ9Lp2SSFnoaRkvNUKGYcz4";


        FaceEngine faceEngine = new FaceEngine("C:\\Users\\zkl1\\Desktop\\ArcSoft_ArcFace_Java_Windows_x64_V3.0\\libs\\WIN64");
        //激活引擎
        int errorCode = faceEngine.activeOnline(appId, sdkKey);

        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }


        ActiveFileInfo activeFileInfo=new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);


        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }

        // 图像路径提取
        ArrayList<File> fileList = getFiles("F:\\pic2");
        ArrayList<String> iconNameList = new ArrayList<String>();//返回文件名数组
        for(int i=0;i<fileList.size();i++){
            String curpath = fileList.get(i).getPath();//获取文件路径
            iconNameList.add(curpath.substring(curpath.lastIndexOf("\\")+1));//将文件名加入数组
        }


        String[] list = iconNameList.toArray(new String[]{});
        //补零
        for (int i=0;i<2500;i++){
           list[i]=String.format("%8s",list[i]).replace(" ","0");
        }


        String seq = "F:\\pic2\\"+list[0];
        //排序
        Arrays.sort(list);

        //补齐至路径
        for (int i=0;i<2500;i++) {
            seq = "F:\\\\pic2\\\\" + list[i];
            list[i]= seq ;

        }

        FaceFeature[] facedata=new FaceFeature[2500];

        for(int i=0;i<2500;i++){
            ImageInfo imageInfo = getRGBData(new File(list[i]));
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);

            //特征提取
            FaceFeature faceFeature = new FaceFeature();
            if (faceInfoList.isEmpty())
                continue;
            errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);

            facedata[i]=faceFeature.clone();
        }


        System.out.println("人脸数据写入完毕");


        for (double k=0;k<10;k++) {
            double threshold = 0.50+0.05*k;
            int TP=0,FN=0,FP=0,TN = 0,P=0,N=0;


            for (int i = 0; i < 2500; i++) {
                if (facedata[i] == null)
                    continue;


                FaceFeature targetFaceFeature = new FaceFeature();
                targetFaceFeature.setFeatureData(facedata[i].getFeatureData());

                for (int j = 0; j < 2500; j++) {

                    if (j <= i)
                        continue;

                    //特征比对

                    FaceFeature sourceFaceFeature = new FaceFeature();
                    if (facedata[j] == null)
                        continue;
                    sourceFaceFeature.setFeatureData(facedata[j].getFeatureData());
                    FaceSimilar faceSimilar = new FaceSimilar();

                    errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);


                    if (j/5-i/5<=0 && faceSimilar.getScore() >= threshold)
                        TP++;
                    else if (j/5-i/5<=0 && faceSimilar.getScore() < threshold)
                        FN++;
                    else if (j/5-i/5>0 && faceSimilar.getScore() >= threshold)
                        FP++;
                    else if (j/5-i/5>0 && faceSimilar.getScore() < threshold)
                        TN++;


                    errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);


                }
            }

            P = TP + FN;
            N = FP + TN;
            System.out.printf("threshold:%f\n", threshold);
            System.out.printf("TP:%d\n", TP);
            System.out.printf("FN:%d\n", FN);
            System.out.printf("FP:%d\n", FP);
            System.out.printf("TN:%d\n", TN);


        }
        //引擎卸载
        errorCode = faceEngine.unInit();
    }
}

