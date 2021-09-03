# Projeto de Meeting Room

Projeto final produzido após o Bootcamp Fullstack Developer (Java + Angular) do Santander na Digital Innovation One.

Ainda que inspirado no projeto final proposto originalmente no Bootcamp, este projeto é completamente diferente, porém abrange igualmente todas as matérias ensinadas ao longo do curso.

## Recursos

Permite a criação de salas virtuais para bate-papo em tempo real por meio de mensagens de texto e gravações de voz direto do navegador (sem compatibilidade mobile).

Inspirado nas versões antigas do Skype, no WhatsApp Web e no [projeto Audicord](https://github.com/arj-mat/Audicord).

## Repositório do Frontend em Angular

[https://github.com/arj-mat/meeting-room-frontend](https://github.com/arj-mat/meeting-room-frontend)

## Demonstração

O projeto completo (backend e frontend) se encontra hospedado no Heroku e pode ser acessado para fins de testes demonstrativos através do seguinte link: [https://proj-meeting-room.herokuapp.com/](https://proj-meeting-room.herokuapp.com/).

Note que o servidor pode estar em estado de suspensão devido a inatividade, o que requer cerca de 30 segundos para que um primeiro acesso seja respondido.

## Backend

#### Dependências utilizadas

- Spring Boot JPA
- Spring Boot Web
- Spring Boot WebSocket
- PostgreSQL
- Lombok
- WebJars SockJS
- WebJars Stomp WebSocket
- Jackson (JSON)
- Spring Doc Open API UI
- Google Guava (Hashing)

O backend consiste basicamente em duas implementações de servidores:

Um servidor HTTP, configurado para suportar a navegação com rotas do Angular e alguns métodos de API, como criar uma sala e obter autorização para entrada nas salas;

E e um servidor WebSocket, para as interações em tempo real.

#### Destaques

##### Autenticação com OAuth do Discord

Com prévia experiência utilizando a API do Discord em Node.JS, implementei um recurso para obter nome de usuário e avatar de uma conta do Discord e utilizar essas informações ao entrar em uma sala.

##### Sincronização de conteúdo não armazenado no servidor

Ao enviar uma gravação de voz, o arquivo de áudio OGG é transmitido para os membros da sala em forma de uma string codificada em Base 64, mas seu conteúdo não é armazenado no servidor.

São persistidas no banco de dados apenas informações como ID da sala, ID do autor e hash do conteúdo do áudio.

Porém, caso um membro tenha entrado na sala após um áudio ter sido enviado, ele ainda poderá ter acesso ao mesmo devido à seguinte lógica:

![](https://i.imgur.com/n14guuM.png)

Esse modelo de sincronização é inspirado nas versões antigas do Skype, que requeriam a presença de membros online para carregar mensagens enviadas enquanto o usuário solicitante estivesse off-line.

Atualmente, os áudios ficam armazenados somente como variáveis na memória volátil.