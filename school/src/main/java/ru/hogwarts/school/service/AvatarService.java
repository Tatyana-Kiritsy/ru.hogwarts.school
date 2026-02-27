package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE_NEW;


@Service
@Transactional
public class AvatarService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    final private AvatarRepository avatarRepository;
    final private StudentRepository studentRepository;

    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

//    @PostConstruct  // ← Выполнится при запуске приложения
//    public void init() {
//        try {
//            Path uploadPath = Path.of(avatarsDir);
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//                System.out.println("✅ Папка создана: " + uploadPath.toAbsolutePath());
//            }
//        } catch (IOException e) {
//            System.err.println("❌ Не удалось создать папку: " + e.getMessage());
//        }
//    }


    public Avatar findAvatar(Long studentId) {
        logger.info("FindAvatar method was invoked");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("UploadAvatar method was invoked");
        Student student = studentRepository.getById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(Objects.requireNonNull(file.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        //  avatar.setData(file.getBytes());
        avatar.setData(generateDataForDB(filePath));

        avatarRepository.save(avatar);
    }

    public byte[] generateDataForDB(Path filePath) throws IOException {
        logger.info("GenerateDataForDB method was invoked");
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();
            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public long getFirstLong() {
        long firstStart = System.currentTimeMillis();

        logger.info("The first method invoked");

        long sum = Stream.iterate(1L, a -> a + 1L)
                .limit(1_000_000)
                .reduce(0L, Long::sum);
        long firstFinish = System.currentTimeMillis() - firstStart;

        logger.info("The first method time {}", firstFinish);

        return sum;
    }

    public long getSecondLong() {
        long secondStart = System.currentTimeMillis();

        logger.info("The second method invoked");

        long sum = LongStream.rangeClosed(1, 1_000_000).parallel()
                .reduce(0L, Long::sum);

        long secondFinish = System.currentTimeMillis() - secondStart;

        logger.info("The second method time {}", secondFinish);

        return sum;
    }

    public String getExtension(String fileName) {
        if (fileName == null) {
            logger.error("Fill in filename!");
            throw new IllegalArgumentException("Имя файла не должно быть пустым!");
        }
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            logger.error("Invalid filename! Extension is blank!");
            throw new IllegalArgumentException("Некорректное имя файла " + fileName + " , отсутсвует расширение!");
        }
        logger.info("getExtension method was invoked");
        return fileName.substring(dotIndex + 1);
    }

    public Collection<Avatar> getAllAvatars(Integer pageNumber, Integer pageSize) {
        logger.info("GetAllAvatars method was invoked");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}


