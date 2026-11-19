# Comunex

Plataforma que conecta moradores, organizações e comunidades, ajudando a identificar necessidades locais e fortalecer a participação da população nas decisões relacionadas aos bairros.

## Integrantes

| Integrante |
|---|
| Carlos Eduardo Sanches Mariano - RM 561756|
| Gabriel Henrique Borges Hombris - RM 566553|
|Icaro Machado de Carvalho - RM 572804|
| Nicolas Guinante - RM 570222|

## Acesso à API

**URL base:** `https://comunex.onrender.com`

## Tecnologias utilizadas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Security OAuth2 Resource Server
- JWT com assinatura RSA
- Bean Validation
- Oracle Database
- Flyway
- Maven

## Funcionalidades

- Cadastro de moradores e organizações.
- Criação de um novo bairro durante o cadastro de um morador.
- Autenticação de usuários por e-mail e senha.
- Geração e validação de tokens JWT.
- Consulta dos dados do próprio usuário autenticado.
- Consulta de usuários pelo ID.
- Listagem de bairros.

## Regras de negócio

- O e-mail deve ser único.
- As senhas são armazenadas como hash, nunca em texto puro.
- Os perfis disponíveis são `MORADOR`, `ORGANIZACAO` e `ADMINISTRADOR`.
- O cadastro público não permite escolher diretamente o perfil `ADMINISTRADOR`.
- Um morador pode se cadastrar em um bairro existente informando seu `idBairro`.
- Caso o bairro ainda não exista, o morador pode informar os dados do novo bairro. O sistema cria o bairro e atribui ao usuário o perfil `ADMINISTRADOR`.
- Organizações podem se cadastrar vinculadas a um bairro existente.
- As operações protegidas exigem um JWT válido.
- A autorização de administradores deve considerar o bairro ao qual estão vinculados.

## Endpoints

Os endpoints abaixo utilizam a URL base informada na seção **Acesso à API**.

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| `POST` | `/api/usuarios/cadastro` | Cadastra um novo usuário | Não |
| `POST` | `/api/auth/login` | Autentica o usuário e retorna o JWT | Não |
| `GET` | `/api/usuarios/me` | Consulta os dados do usuário autenticado | Sim |
| `PATCH` | `/api/usuarios/me` | Atualiza os dados do próprio perfil | Sim |
| `DELETE` | `/api/usuarios/me` | Exclui a própria conta | Sim |
| `GET` | `/api/bairros` | Lista os bairros cadastrados | Conforme configuração da rota |


## Como cadastrar um usuário

O cadastro é público. Portanto, não é necessário fazer login antes dessa requisição.

**Endpoint**

```http
POST /api/usuarios/cadastro
Content-Type: application/json
```

### 1. Cadastro em um bairro existente

Informe o ID de um bairro que já esteja cadastrado:

```json
{
  "nome": "Usuário Teste",
  "email": "teste@email.com",
  "senha": "Senha123!",
  "descricao": "Morador do bairro",
  "tipoPerfil": "MORADOR",
  "idBairro": 1
}
```

### 2. Cadastro criando um novo bairro

Se o bairro ainda não estiver cadastrado, omita `idBairro` e informe os dados do novo bairro:

```json
{
  "nome": "Usuário Teste",
  "email": "teste@email.com",
  "senha": "Senha123!",
  "descricao": "Responsável pela criação do bairro",
  "tipoPerfil": "MORADOR",
  "nomeBairro": "Centro",
  "municipio": "São Paulo",
  "estado": "SP"
}
```

Nesse caso, o sistema cria o bairro e atribui ao usuário o perfil `ADMINISTRADOR`.

### Campos do cadastro

| Campo | Obrigatório | Descrição |
|---|---|---|
| `nome` | Sim | Nome do usuário |
| `email` | Sim | E-mail válido e único |
| `senha` | Sim | Senha com 8 a 72 caracteres |
| `descricao` | Não | Descrição do perfil |
| `tipoPerfil` | Sim | `MORADOR` ou `ORGANIZACAO` no cadastro público |
| `idBairro` | Depende | ID de um bairro existente |
| `nomeBairro` | Depende | Nome do novo bairro |
| `municipio` | Depende | Município do novo bairro |
| `estado` | Depende | UF com duas letras |

## Como fazer login

Depois de cadastrar o usuário, envie o e-mail e a senha cadastrados para autenticação.

**Endpoint**

```http
POST /api/auth/login
Content-Type: application/json
```

**Corpo da requisição**

```json
{
  "email": "teste@email.com",
  "senha": "Senha123!"
}
```

Se as credenciais forem válidas, a API retorna um token JWT.

> Utilize o user acima para fazer login 

## Como acessar endpoints protegidos

Nas requisições protegidas, envie o token JWT no cabeçalho `Authorization`, utilizando o esquema `Bearer`.

**Exemplo: consultar os próprios dados**

```http
GET /api/usuarios/me
Authorization: Bearer SEU_TOKEN_JWT
```

O Spring Security valida o token e identifica o usuário autenticado. Assim, não é necessário enviar o e-mail ou o ID do usuário no corpo da requisição para consultar o próprio perfil.

- Sem token, com token inválido ou expirado: acesso não autorizado.
- Com token válido: a requisição pode prosseguir, respeitando as permissões do endpoint.

## Códigos de resposta HTTP

| Código | Significado |
|---|---|
| `200 OK` | Requisição realizada com sucesso |
| `201 Created` | Cadastro realizado com sucesso |
| `400 Bad Request` | Dados inválidos ou incompletos |
| `401 Unauthorized` | Token ausente, inválido ou expirado; ou credenciais inválidas |
| `403 Forbidden` | Usuário autenticado, mas sem permissão |
| `404 Not Found` | Recurso não encontrado |
| `409 Conflict` | E-mail já cadastrado |

## Segurança

- As senhas são protegidas com `PasswordEncoder`.
- A autenticação utiliza JWT com assinatura RSA.
- O token deve ser enviado no cabeçalho `Authorization` como `Bearer`.
- As chaves privadas e demais credenciais não devem ser publicadas no repositório.
- As permissões administrativas devem respeitar o bairro associado ao administrador.

---

**Comunex — conectando pessoas e fortalecendo comunidades.**
