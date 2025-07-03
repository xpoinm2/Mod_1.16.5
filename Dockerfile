# Используем образ Ubuntu как основу для мульти-языковой среды
FROM ubuntu:24.04

# Устанавливаем основные утилиты и языки сразу одним слоем
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    openjdk-8-jdk \
    python3.12 python3-pip \
    nodejs npm \
    ruby-full \
    curl build-essential \
 && rm -rf /var/lib/apt/lists/* \
# Устанавливаем Rust через rustup
 && curl https://sh.rustup.rs -sSf | sh -s -- -y \
 && . $HOME/.cargo/env \
# Устанавливаем Go
 && wget https://go.dev/dl/go1.23.8.linux-amd64.tar.gz \
 && tar -C /usr/local -xzf go1.23.8.linux-amd64.tar.gz \
 && rm go1.23.8.linux-amd64.tar.gz \
# Устанавливаем Swift
 && apt-get update \
 && apt-get install -y --no-install-recommends \
    clang libicu-dev libcurl4-openssl-dev libpython3.12 \
 && rm -rf /var/lib/apt/lists/* \
# Создаём симлинки и настраиваем окружение
 && ln -s /usr/local/go/bin/go /usr/bin/go \
 && ln -s /usr/local/go/bin/gofmt /usr/bin/gofmt

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