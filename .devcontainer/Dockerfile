FROM mcr.microsoft.com/devcontainers/java:21-bullseye

USER root

# Remove o repositório do Yarn que está causando o erro de chave GPG
RUN rm -f /etc/apt/sources.list.d/yarn.list \
    && rm -f /etc/apt/sources.list.d/nodesource.list

# Cria a pasta do Maven e define o usuário vscode como dono
RUN mkdir -p /home/vscode/.m2 \
    && chown -R vscode:vscode /home/vscode/.m2

# Definir o usuário para evitar problemas de permissão de root
USER vscode

# Instalar o Quarkus CLI através do script oficial
RUN curl -Ls https://sh.jbang.dev | bash -s - trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/ \
    && curl -Ls https://sh.jbang.dev | bash -s - app install --force quarkus@quarkusio

# Adicionar o Quarkus CLI ao PATH do usuário
ENV PATH="/home/vscode/.jbang/bin:${PATH}"

# Definir o diretório de trabalho padrão
WORKDIR /workspaces