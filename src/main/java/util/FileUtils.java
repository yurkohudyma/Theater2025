package util;

import java.nio.file.Path;

public class FileUtils {
    private FileUtils() {
    }

    /**
     * Створює відносний шлях до файлу, відносно базового шляху.
     *
     * @param basePath  Базовий каталог, відносно якого будується відносний шлях.
     * @param filePath  Абсолютний шлях до файлу.
     * @return Відносний шлях до файлу.
     */
    public static String buildRelativeUrl(Path basePath, Path filePath) {
        // Перевірка, чи базовий шлях і файл є коректними
        if (basePath == null || filePath == null) {
            throw new IllegalArgumentException("Paths must not be null");
        }

        // Переводимо шляхи в абсолютні
        Path absoluteBasePath = basePath.toAbsolutePath();
        Path absoluteFilePath = filePath.toAbsolutePath();

        // Отримуємо відносний шлях
        Path relativePath = absoluteBasePath.relativize(absoluteFilePath);

        // Формуємо URL (можна додати '/' на початку і замінити слеші для кросплатформності)
        return "/" + relativePath.toString().replace("\\", "/");
    }
}

