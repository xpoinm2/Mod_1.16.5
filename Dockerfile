# Используем образ с предустановленным JDK и нужными утилитами
FROM openjdk:11-jdk

# Устанавливаем git (если понадобится внутри контейнера)
RUN apt-get update \
 && apt-get install -y git \
 && rm -rf /var/lib/apt/lists/*

# Устанавливаем Gradle (или используем wrapper из проекта)
# Если вы используете gradle-wrapper, этот шаг можно опустить.
RUN wget https://services.gradle.org/distributions/gradle-7.3.3-bin.zip \
 && unzip gradle-7.3.3-bin.zip -d /opt \
 && ln -s /opt/gradle-7.3.3/bin/gradle /usr/bin/gradle \
 && rm gradle-7.3.3-bin.zip

# Создаём рабочую директорию внутри контейнера
WORKDIR /workspace

# Копируем исходники проекта внутрь контейнера
COPY . /workspace

# По умолчанию запускаем сборку Gradle. При необходимости замените на вашу команду.
CMD ["gradle", "build", "--no-daemon"]