# Projeto de Meeting Room

Projeto final produzido após o Bootcamp Fullstack Developer (Java + Angular) do Santander na Digital Innovation One.

Ainda que inspirado no projeto final proposto originalmente no Bootcamp, este projeto é completamente diferente, porém abrange igualmente todas as matérias ensinadas ao longo do curso.

## Índice
- [Projeto de Meeting Room](#projeto-de-meeting-room)
  * [Recursos](#recursos)
  * [Demonstração](#demonstra--o)
  * [Repositório do Frontend em Angular](#reposit-rio-do-frontend-em-angular)
  * [Backend](#backend)
      - [Dependências utilizadas](#depend-ncias-utilizadas)
      - [Destaques](#destaques)
        * [Autenticação com OAuth do Discord](#autentica--o-com-oauth-do-discord)
        * [Sincronização de conteúdo não armazenado no servidor](#sincroniza--o-de-conte-do-n-o-armazenado-no-servidor)
  * [Instruções para clonagem do backend](#instru--es-para-clonagem-do-backend)
    + [Variável da porta do servidor HTTP](#vari-vel-da-porta-do-servidor-http)
    + [Variáveis de ambiente do banco de dados](#vari-veis-de-ambiente-do-banco-de-dados)
    + [Parâmetros da API do Discord](#par-metros-da-api-do-discord)


## Recursos

Permite a criação de salas virtuais para bate-papo em tempo real por meio de mensagens de texto e gravações de voz direto do navegador (sem compatibilidade mobile).

Inspirado nas versões antigas do Skype, no WhatsApp Web e no [projeto Audicord](https://github.com/arj-mat/Audicord).

## Demonstração

![](https://i.imgur.com/e6XpeBK.png)

O projeto completo (backend e frontend) se encontra hospedado no Heroku e pode ser acessado para fins de testes demonstrativos através do seguinte link: [https://proj-meeting-room.herokuapp.com/](https://proj-meeting-room.herokuapp.com/).

Note que o servidor pode estar em estado de suspensão devido a inatividade, o que requer cerca de 30 segundos para que um primeiro acesso seja respondido.

## Repositório do Frontend em Angular

[https://github.com/arj-mat/meeting-room-frontend](https://github.com/arj-mat/meeting-room-frontend)

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

## Instruções para clonagem do backend

Versão do Java: 11.

O frontend compilado está incluído na pasta [static](https://github.com/arj-mat/meeting-room/tree/master/src/main/resources/static).

Algumas variáveis de ambiente são necessárias para o funcionamento do servidor.

### Variável da porta do servidor HTTP

Defina a variável de ambiente `PORT` com o número da porta desejada.

No ambiente do Heroku, esta variável é providenciada automaticamente.

### Variáveis de ambiente do banco de dados

O arquivo [application.yml](https://github.com/arj-mat/meeting-room/blob/master/src/main/resources/application.yml) faz referência às variáveis de ambiente necessárias para conexão ao banco de dados PostgreSQL.

`${DATABASE_URL}` é a variável providenciada pelo Heroku contendo todas as informações necessárias para a conexão.

Em caso de uma conexão local, defina a variável `DATABASE_URL` como `jdbc:postgresql://localhost:5432/NOME_DO_BANCO_DE_DADOS`.

Defina também as variáveis `DB_USERNAME` e `DB_PASSWORD` com respectivamente o nome de usuário e senha do seu banco local. 

Remova o comentário dessas duas variáveis no arquivo [application.yml](https://github.com/arj-mat/meeting-room/blob/master/src/main/resources/application.yml).

### Parâmetros da API do Discord

O arquivo [AppConfig.java](https://github.com/arj-mat/meeting-room/blob/master/src/main/java/com/santander/meeting/meetingroom/AppConfig.java) define as credenciais necessárias para utilizar a API do Discord.

Caso queira utilizar o recurso "Entrar com Discord" localmente, obtenha suas credenciais de aplicativo em https://discord.com/developers/docs/intro.

Substitua a string `DISCORD_OAUTH_CLIENT_ID` pelo Client ID do seu aplicativo.

Defina a variável de ambiente `DISCORD_OAUTH_URI` com a URI de redirecionamento definida no seu painel do aplicativo do Discord.

Defina a variável de ambiente `DISCORD_API_SECRET` com o token secreto do seu aplicativo.