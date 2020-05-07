package com.upmusic.web.services;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.microsoft.azure.storage.blob.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.io.IOUtils;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.media.MediaConfiguration;
import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.MediaService;
import com.microsoft.windowsazure.services.media.WritableBlobContainerContract;
import com.microsoft.windowsazure.services.media.authentication.AzureAdClientSymmetricKey;
import com.microsoft.windowsazure.services.media.authentication.AzureAdTokenCredentials;
import com.microsoft.windowsazure.services.media.authentication.AzureAdTokenProvider;
import com.microsoft.windowsazure.services.media.authentication.AzureEnvironments;
import com.microsoft.windowsazure.services.media.models.AccessPolicy;
import com.microsoft.windowsazure.services.media.models.AccessPolicyInfo;
import com.microsoft.windowsazure.services.media.models.AccessPolicyPermission;
import com.microsoft.windowsazure.services.media.models.Asset;
import com.microsoft.windowsazure.services.media.models.AssetFile;
import com.microsoft.windowsazure.services.media.models.AssetFileInfo;
import com.microsoft.windowsazure.services.media.models.AssetInfo;
import com.microsoft.windowsazure.services.media.models.Job;
import com.microsoft.windowsazure.services.media.models.JobInfo;
import com.microsoft.windowsazure.services.media.models.JobState;
import com.microsoft.windowsazure.services.media.models.ListResult;
import com.microsoft.windowsazure.services.media.models.Locator;
import com.microsoft.windowsazure.services.media.models.LocatorInfo;
import com.microsoft.windowsazure.services.media.models.LocatorType;
import com.microsoft.windowsazure.services.media.models.MediaProcessor;
import com.microsoft.windowsazure.services.media.models.MediaProcessorInfo;
import com.microsoft.windowsazure.services.media.models.Task;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.helper.UPMusicHelper;

import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

@Service
public class AzureStorageServiceImpl implements AzureStorageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String MEDIA_TYPE_AUDIO = "audio";
    private final String MEDIA_TYPE_VIDEO = "video";

    @Autowired
    private CloudStorageAccount storageAccount;

    @Value("${upm.azure.storage.resource.url}")
    private String bucketResourceUrl;

    private MediaContract mediaService;
    // private String preferedEncoderAudio = "Media Encoder Standard";
    // private String encodingPresetAudio = "AAC Good Quality Audio for Download";
    private String preferedEncoderVideo = "Media Encoder Standard";
    private String encodingPresetVideo = "H264 Single Bitrate 720p";

    /**
     * S3 리소스 파일 업로드
     */
    @Override
    public void uploadResource(MultipartFile multipartFile, String prefix) {
        if (!StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            try {
                uploadToResource(multipartFile.getInputStream(),
                        prefix + UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadResource(InputStream inputStream, String originalFileName, String prefix) {
        if (!StringUtils.isEmpty(originalFileName)) {
            try {
                uploadToResource(inputStream,
                        prefix + UPMusicHelper.makeReadableUrl(originalFileName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String uploadResource(String url, String prefix) {
        logger.debug("uploadResource : url is {}", url);
        String filename = null;
        if (url != null && !url.isEmpty()) {
            if (url.contains("fbsbx.com") || url.contains("graph.facebook.com")) {
                filename = "facebook.jpeg";
            } else {
                filename = url.substring(url.lastIndexOf('/') + 1, url.length());
                if (filename.contains("?"))
                    filename = filename.substring(0, url.lastIndexOf('?') + 1);
                if (!filename.contains("."))
                    filename += ".jpg";
            }
            try (InputStream in = new URL(url).openStream()) {
                uploadToResource(in, prefix + filename);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                filename = null;
            } catch (IOException e) {
                e.printStackTrace();
                filename = null;
            }
        }
        return filename;
    }

    /**
     * S3 음원 파일 업로드
     */
    @Override
    public void uploadTrackToTranscode(MultipartFile multipartFile, String prefix) {
        if (!StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            try {
                // 라이센스 판매용으로 원본을 트랙 버킷에 업로드
                uploadToTrack(multipartFile.getInputStream(),
                        prefix + UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
                // 재생용으로 mp3 변환
                if (!multipartFile.getOriginalFilename().endsWith("zip")
                        && !multipartFile.getOriginalFilename().endsWith("mp3")) {
                    uploadToTranscodeAudio(multipartFile.getInputStream(),
                            UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()), prefix);
                }
            } catch (IOException | ServiceException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * S3 음원 파일 업로드 with fileNamne
     */
    @Override
    public void uploadTrackToTranscode(MultipartFile multipartFile, String fileName, String prefix) {
        if (!StringUtils.isEmpty(fileName)) {
            try {
                // 라이센스 판매용으로 원본을 트랙 버킷에 업로드
                uploadToTrack(multipartFile.getInputStream(),
                        prefix + UPMusicHelper.makeReadableUrl(fileName));
                // 재생용으로 mp3 변환
                if (!fileName.endsWith("zip")
                        && !fileName.endsWith("mp3")) {
                    uploadToTranscodeAudio(multipartFile.getInputStream(),
                            UPMusicHelper.makeReadableUrl(fileName), prefix);
                }
            } catch (IOException | ServiceException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * S3 영상 파일 업로드 영상 변환 필요할 경우
     */
    @Override
    public void uploadVideoToTranscode(MultipartFile multipartFile, String prefix) {
        if (!StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            try {
                // mp4 파일의 경우엔 변환 없이 바로 리소스 버킷에 업로드
                if (multipartFile.getOriginalFilename().endsWith("mp4")) {
                    uploadToResource(multipartFile.getInputStream(),
                            prefix + UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()));
                } else {
                    uploadToTranscodeVideo(multipartFile.getInputStream(),
                            UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename()), prefix);
                    // String origFilename =
                    // UPMusicHelper.makeReadableUrl(multipartFile.getOriginalFilename());
                    // int i = origFilename.lastIndexOf('.');
                    // if (0 < i) {
                    // String extension = origFilename.substring(i);
                    // origFilename = origFilename.replaceAll(extension, ".mp4");
                    // } else {
                    // origFilename += ".mp4";
                    // }
                    // uploadToTranscodeVideo(multipartFile.getInputStream(), prefix +
                    // origFilename);
                }
            } catch (IOException | ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    public ResponseEntity<byte[]> downloadMusicFile(String prefix, String filename) throws IOException {
        // logger.debug("downloadMusicFile : key is {}", prefix + filename);
        // GetObjectRequest getObjectRequest = new GetObjectRequest(bucketTrack, prefix
        // + filename);
        // S3Object s3Object = s3client.getObject(getObjectRequest);
        // S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        // byte[] bytes = IOUtils.toByteArray(objectInputStream);
        //
        // HttpHeaders httpHeaders = new HttpHeaders();
        // httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // httpHeaders.setContentLength(bytes.length);
        // httpHeaders.setContentDispositionFormData("attachment", filename);
        // return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<byte[]> downloadVocalGuideFile(String prefix, String filename) throws IOException {
        logger.debug("downloadVocalGuideFile : key is {}", prefix + filename);
        try {
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER);

            CloudBlockBlob blob = container.getBlockBlobReference(prefix + filename);
            byte[] bytes = IOUtils.toByteArray(blob.openInputStream());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", filename);
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, null, HttpStatus.FORBIDDEN);
    }

    /**
     * 리소스 버킷에 바로 업로드
     *
     * @param inputStream
     * @param uploadKey
     */
    @Override
    public void uploadToResource(InputStream fileInputStream, String uploadKey) {

        try {
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER);

            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            CloudBlockBlob blob = container.getBlockBlobReference(uploadKey);
            BlobOutputStream blobOutputStream = blob.openOutputStream();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            int next = inputStream.read();
            while (next != -1) {
                blobOutputStream.write(next);
                next = inputStream.read();
            }
            blobOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 트랙 버킷에 바로 업로드
     *
     * @param inputStream
     * @param uploadKey
     */
    @Override
    public void uploadToTrack(InputStream fileInputStream, String uploadKey) {

        try {
            CloudStorageAccount uploadStorageAccount = CloudStorageAccount
                    .parse(UPMusicConstants.AZURE_STORAGE_CONNECT_STRING_ALBUM);
            CloudBlobClient blobClient = uploadStorageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient
                    .getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER_ALBUM);

            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            CloudBlockBlob blob = container.getBlockBlobReference(uploadKey);
            BlobOutputStream blobOutputStream = blob.openOutputStream();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            int next = inputStream.read();
            while (next != -1) {
                blobOutputStream.write(next);
                next = inputStream.read();
            }
            blobOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 미디어 서비스 연동 - MP3
     *
     * @param inputStream
     * @param uploadKey
     * @throws UnsupportedAudioFileException
     */
    private void uploadToTranscodeAudio(InputStream fileInputStream, String filename, String prefix)
            throws ServiceException, IOException, UnsupportedAudioFileException {

        logger.debug("uploadToTranscodeAudio");
        String uploadKey = prefix + filename;
        int i = uploadKey.lastIndexOf('.');
        if (0 < i) {
            String extension = uploadKey.substring(i);
            uploadKey = uploadKey.replaceAll(extension, ".mp3");
        } else {
            uploadKey += ".mp3";
        }

        try {
            CloudStorageAccount albumStorageAccount = CloudStorageAccount
                    .parse(UPMusicConstants.AZURE_STORAGE_CONNECT_STRING_ALBUM);
            CloudBlobClient albumBlobClient = albumStorageAccount.createCloudBlobClient();
            CloudBlobContainer albumContainer = albumBlobClient
                    .getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER_ALBUM);

            CloudBlockBlob albumBlob = albumContainer.getBlockBlobReference(uploadKey);
            BlobOutputStream blobOutputStream = albumBlob.openOutputStream();

            BufferedInputStream bufferedIn = new BufferedInputStream(fileInputStream);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            AudioFormat baseFormat = audioStream.getFormat();
            logger.debug("uploadToTranscodeAudio : baseFormat.getEncoding() is {}", baseFormat.getEncoding());
            baseFormat = new AudioFormat(baseFormat.getEncoding(), baseFormat.getSampleRate(), 16,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
                    baseFormat.isBigEndian());
            LameEncoder encoder = new LameEncoder(baseFormat, 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
            byte[] buffer = new byte[encoder.getPCMBufferSize()];
            byte[] pcm = IOUtils.toByteArray(audioStream);
            int bytesToTransfer = Math.min(buffer.length, pcm.length);
            logger.debug("uploadToTranscodeAudio buffer.length is {} and pcm.length is {}: ", buffer.length,
                    pcm.length);
            int bytesWritten;
            int currentPcmPosition = 0;
            while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
                currentPcmPosition += bytesToTransfer;
                bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);
                blobOutputStream.write(buffer, 0, bytesWritten);
            }

            blobOutputStream.close();
            encoder.close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("uploadToTranscodeAudio : e is {}", e.toString());
        }
    }

    /**
     * 미디어 서비스 연동 - 비디오
     *
     * @param inputStream
     * @param uploadKey
     */
    private void uploadToTranscodeVideo(final InputStream fileInputStream, String filename, String prefix)
            throws ServiceException, IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            logger.debug("clientId: " + UPMusicConstants.AZURE_MEDIA_SERVICES_CLIENT_ID);
            logger.debug("clientKey: " + UPMusicConstants.AZURE_MEDIA_SERVICES_CLIENT_KEY);
            // Setup Azure AD Service Principal Symmetric Key Credentials
            AzureAdTokenCredentials credentials = new AzureAdTokenCredentials(
                    UPMusicConstants.AZURE_MEDIA_SERVICES_TENANT,
                    new AzureAdClientSymmetricKey(UPMusicConstants.AZURE_MEDIA_SERVICES_CLIENT_ID,
                            UPMusicConstants.AZURE_MEDIA_SERVICES_CLIENT_KEY),
                    AzureEnvironments.AZURE_CLOUD_ENVIRONMENT);

            AzureAdTokenProvider provider = new AzureAdTokenProvider(credentials, executorService);

            // Create a new configuration with the credentials
            Configuration configuration = MediaConfiguration.configureWithAzureAdTokenProvider(
                    new URI(UPMusicConstants.AZURE_MEDIA_SERVICES_REST_API_ENDPOINT), provider);

            // Create the media service provisioned with the new configuration
            mediaService = MediaService.create(configuration);

            // Upload a local file to an Asset
            AssetInfo uploadAsset = uploadFileAndCreateAsset(fileInputStream, filename);
            logger.debug("Uploaded Asset Id: " + uploadAsset.getId());

            // Transform the Asset
            AssetInfo encodedAsset = encodeVideo(uploadAsset);
            logger.debug("Encoded Asset Id: " + encodedAsset.getId());

            // Copy asset to resource storage
            copyToResource(encodedAsset, MEDIA_TYPE_VIDEO, prefix + filename);

            cleanup();

            logger.debug("Sample completed!");

        } catch (ServiceException se) {
            logger.error("ServiceException encountered.");
            logger.error(se.toString());
        } catch (Exception e) {
            logger.error("Exception encountered.");
            logger.error(e.toString());
        } finally {
            executorService.shutdown();
        }
    }

    private AssetInfo uploadFileAndCreateAsset(final InputStream fileInputStream, String uploadKey)
            throws ServiceException {

        WritableBlobContainerContract uploader;
        AssetInfo resultAsset;
        AccessPolicyInfo uploadAccessPolicy;
        LocatorInfo uploadLocator = null;

        // Create an Asset
        resultAsset = mediaService.create(Asset.create().setName(uploadKey).setAlternateId("altId"));
        logger.debug("Created Asset " + uploadKey);

        // Create an AccessPolicy that provides Write access for 15 minutes
        uploadAccessPolicy = mediaService
                .create(AccessPolicy.create("uploadAccessPolicy", 15.0, EnumSet.of(AccessPolicyPermission.WRITE)));

        // Create a Locator using the AccessPolicy and Asset
        uploadLocator = mediaService
                .create(Locator.create(uploadAccessPolicy.getId(), resultAsset.getId(), LocatorType.SAS));

        // Create the Blob Writer using the Locator
        uploader = mediaService.createBlobWriter(uploadLocator);

        logger.debug("Uploading " + uploadKey);

        // Upload the local file to the media asset
        uploader.createBlockBlob(uploadKey, fileInputStream);

        // Inform Media Services about the uploaded files
        mediaService.action(AssetFile.createFileInfos(resultAsset.getId()));
        logger.debug("Uploaded Asset File " + uploadKey);

        mediaService.delete(Locator.delete(uploadLocator.getId()));
        mediaService.delete(AccessPolicy.delete(uploadAccessPolicy.getId()));

        return resultAsset;
    }

    /*
     * Azure 지원 안함 private AssetInfo encodeAudio(AssetInfo assetToEncode) throws
     * ServiceException, InterruptedException {
     *
     * // Retrieve the list of Media Processors that match the name
     * ListResult<MediaProcessorInfo> mediaProcessors = mediaService
     * .list(MediaProcessor.list().set("$filter", String.format("Name eq '%s'",
     * preferedEncoderAudio)));
     *
     * // Use the latest version of the Media Processor MediaProcessorInfo
     * mediaProcessor = null; for (MediaProcessorInfo info : mediaProcessors) { if
     * (null == mediaProcessor ||
     * info.getVersion().compareTo(mediaProcessor.getVersion()) > 0) {
     * mediaProcessor = info; } }
     *
     * logger.debug("Using Media Processor: " + mediaProcessor.getName() + " " +
     * mediaProcessor.getVersion());
     *
     * // Create a task with the specified Media Processor String outputAssetName =
     * String.format("%s as %s", assetToEncode.getName(), encodingPresetAudio);
     * String taskXml = "<taskBody><inputAsset>JobInputAsset(0)</inputAsset>" +
     * "<outputAsset assetCreationOptions=\"0\"" // AssetCreationOptions.None +
     * " assetName=\"" + outputAssetName +
     * "\">JobOutputAsset(0)</outputAsset></taskBody>";
     *
     * Task.CreateBatchOperation task = Task.create(mediaProcessor.getId(), taskXml)
     * .setConfiguration(encodingPresetAudio).setName("Encoding");
     *
     * // Create the Job; this automatically schedules and runs it. Job.Creator
     * jobCreator = Job.create() .setName(String.format("Encoding %s to %s",
     * assetToEncode.getName(), encodingPresetAudio))
     * .addInputMediaAsset(assetToEncode.getId()).setPriority(2).addTaskCreator(task
     * ); JobInfo job = mediaService.create(jobCreator);
     *
     * String jobId = job.getId(); logger.debug("Created Job with Id: " + jobId);
     *
     * // Check to see if the Job has completed checkJobStatus(jobId); // Done with
     * the Job
     *
     * // Retrieve the output Asset ListResult<AssetInfo> outputAssets =
     * mediaService.list(Asset.list(job.getOutputAssetsLink())); return
     * outputAssets.get(0); }
     */

    private AssetInfo encodeVideo(AssetInfo assetToEncode) throws ServiceException, InterruptedException {

        // Retrieve the list of Media Processors that match the name
        ListResult<MediaProcessorInfo> mediaProcessors = mediaService
                .list(MediaProcessor.list().set("$filter", String.format("Name eq '%s'", preferedEncoderVideo)));

        // Use the latest version of the Media Processor
        MediaProcessorInfo mediaProcessor = null;
        for (MediaProcessorInfo info : mediaProcessors) {
            if (null == mediaProcessor || info.getVersion().compareTo(mediaProcessor.getVersion()) > 0) {
                mediaProcessor = info;
            }
        }

        logger.debug("Using Media Processor: " + mediaProcessor.getName() + " " + mediaProcessor.getVersion());

        // Create a task with the specified Media Processor
        String outputAssetName = String.format("%s as %s", assetToEncode.getName(), encodingPresetVideo);
        String taskXml = "<taskBody><inputAsset>JobInputAsset(0)</inputAsset>"
                + "<outputAsset assetCreationOptions=\"0\"" // AssetCreationOptions.None
                + " assetName=\"" + outputAssetName + "\">JobOutputAsset(0)</outputAsset></taskBody>";

        Task.CreateBatchOperation task = Task.create(mediaProcessor.getId(), taskXml)
                .setConfiguration(encodingPresetVideo).setName("Encoding");

        // Create the Job; this automatically schedules and runs it.
        Job.Creator jobCreator = Job.create()
                .setName(String.format("Encoding %s to %s", assetToEncode.getName(), encodingPresetVideo))
                .addInputMediaAsset(assetToEncode.getId()).setPriority(2).addTaskCreator(task);
        JobInfo job = mediaService.create(jobCreator);

        String jobId = job.getId();
        logger.debug("Created Job with Id: " + jobId);

        // Check to see if the Job has completed
        checkJobStatus(jobId);
        // Done with the Job

        // Retrieve the output Asset
        ListResult<AssetInfo> outputAssets = mediaService.list(Asset.list(job.getOutputAssetsLink()));
        return outputAssets.get(0);
    }

    private void copyToResource(AssetInfo encodedAsset, String mediaType, String uploadKey) throws ServiceException,
            URISyntaxException, FileNotFoundException, StorageException, IOException, InvalidKeyException {

        int i = uploadKey.lastIndexOf('.');
        if (0 < i) {
            String extension = uploadKey.substring(i);
            if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
                uploadKey = uploadKey.replaceAll(extension, ".mp3");
            } else {
                uploadKey = uploadKey.replaceAll(extension, ".mp4");
            }
        } else {
            uploadKey += mediaType.equals(MEDIA_TYPE_AUDIO) ? ".mp3" : ".mp4";
        }

        AccessPolicyInfo downloadAccessPolicy = null;

        downloadAccessPolicy = mediaService
                .create(AccessPolicy.create("Download", 15.0, EnumSet.of(AccessPolicyPermission.READ)));
        logger.debug("Created download access policy with id: " + downloadAccessPolicy.getId());

        LocatorInfo downloadLocator = null;
        downloadLocator = mediaService
                .create(Locator.create(downloadAccessPolicy.getId(), encodedAsset.getId(), LocatorType.SAS));
        logger.debug("Created download locator with id: " + downloadLocator.getId());

        logger.debug("Accessing the output files of the encoded asset.");
        // Iterate through the files associated with the encoded asset.
        for (AssetFileInfo assetFile : mediaService.list(AssetFile.list(encodedAsset.getAssetFilesLink()))) {
            String fileName = assetFile.getName();
            if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
                if (0 >= fileName.indexOf(".mp3"))
                    continue;
            } else {
                if (0 >= fileName.indexOf(".mp4"))
                    continue;
            }

            logger.debug("Downloading file: " + fileName + ". ");
            String locatorPath = downloadLocator.getPath();
            int startOfSas = locatorPath.indexOf("?");
            String blobPath = locatorPath + fileName;
            if (startOfSas >= 0) {
                blobPath = locatorPath.substring(0, startOfSas) + "/" + fileName + locatorPath.substring(startOfSas);
            }
            // Copy to resource storage
            URI baseuri = new URI(blobPath);
            CloudBlockBlob sasBlob = new CloudBlockBlob(baseuri);
            logger.debug("sasBlob is {}", sasBlob.getName());

            if (mediaType.equals(MEDIA_TYPE_AUDIO)) {
                CloudStorageAccount albumStorageAccount = CloudStorageAccount
                        .parse(UPMusicConstants.AZURE_STORAGE_CONNECT_STRING_ALBUM);
                CloudBlobClient albumBlobClient = albumStorageAccount.createCloudBlobClient();
                CloudBlobContainer albumContainer = albumBlobClient
                        .getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER_ALBUM);
                CloudBlockBlob albumBlob = albumContainer.getBlockBlobReference(uploadKey);
                logger.debug("albumBlob.startCopy(sasBlob) ...");
                albumBlob.startCopy(sasBlob);
            } else {
                CloudBlobClient resourceBlobClient = storageAccount.createCloudBlobClient();
                CloudBlobContainer resourceContainer = resourceBlobClient
                        .getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER);
                CloudBlockBlob resourceBlob = resourceContainer.getBlockBlobReference(uploadKey);
                logger.debug("resourceBlob.startCopy(sasBlob) ...");
                resourceBlob.startCopy(sasBlob);
            }
            // resourceBlob.startCopy(baseuri);
        }

        logger.debug("Deleting download locator and access policy.");
        mediaService.delete(Locator.delete(downloadLocator.getId()));
        mediaService.delete(AccessPolicy.delete(downloadAccessPolicy.getId()));
    }

    // Remove all assets from your Media Services account.
    // You could instead remove assets by name or ID, etc., but for
    // simplicity this example removes all of them.
    private void cleanup() throws ServiceException {
        // Retrieve a list of all assets.
        List<AssetInfo> assets = mediaService.list(Asset.list());
        // Iterate through the list, deleting each asset.
        for (AssetInfo asset : assets) {
            logger.debug("Deleting asset named " + asset.getName() + " (" + asset.getId() + ")");
            mediaService.delete(Asset.delete(asset.getId()));
        }
    }

    // Helper function to check to on the status of the job.
    private void checkJobStatus(String jobId) throws InterruptedException, ServiceException {
        int maxRetries = 12; // Number of times to retry. Small jobs often take 2 minutes.
        JobState jobState = null;
        while (maxRetries > 0) {
            Thread.sleep(10000); // Sleep for 10 seconds, or use another interval.
            // Determine the job state.
            jobState = mediaService.get(Job.get(jobId)).getState();
            logger.debug("Job state is " + jobState);
            if (jobState == JobState.Finished || jobState == JobState.Canceled || jobState == JobState.Error) {
                // The job is done.
                break;
            }
            // The job is not done. Sleep and loop if max retries
            // has not been reached.
            maxRetries--;
        }
    }

    /* get Blob from azure storage
     *  bulk 컨테이너에 파일을 업로드 후, 엑셀파일과 대조하여 실제 앨범 / 음원 컨테아너로 옮기는 작업 할때 사용
     */
    public Map<String, byte[]> getBlob(String imgNAme, String fileName){
        Map<String, byte[]> map = new HashMap<>();
        try {
            // bulk 컨테이너의 폴더명.
            String[] folders = {"image", "music", "track"};
            // 각 파일의 확장자는 통일 할 것. (앨범커버 : .png, 음원 : .mp3, 트랙모음 : .zip)
            String[] extensions = {".png", ".mp3", ".zip"};
            CloudStorageAccount albumStorageAccount = CloudStorageAccount.parse(UPMusicConstants.AZURE_STORAGE_CONNECT_STRING_ALBUM);
            CloudBlobClient albumBlobClient = albumStorageAccount.createCloudBlobClient();
            CloudBlobContainer fromContainer = albumBlobClient.getContainerReference(UPMusicConstants.AZURE_STORAGE_CONTAINER_BULK);

            CloudBlockBlob imgBlob = fromContainer.getBlockBlobReference(folders[0] + "/" + imgNAme + extensions[0]);
            CloudBlockBlob mp3Blob = fromContainer.getBlockBlobReference(folders[1] + "/" + fileName + extensions[1]);
            CloudBlockBlob zipBlob = fromContainer.getBlockBlobReference(folders[2] + "/" + fileName + extensions[2]);

            byte[] imgBytes = IOUtils.toByteArray(imgBlob.openInputStream());
            byte[] mp3Bytes = IOUtils.toByteArray(mp3Blob.openInputStream());
            byte[] zipBytes = IOUtils.toByteArray(zipBlob.openInputStream());

            map.put("image", imgBytes);
            map.put("music", mp3Bytes);
            map.put("track", zipBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
