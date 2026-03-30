# LiferayPokedex
Individual Development Plan (PDI)

# Pokedex em Liferay DXP – Fluxo MVC

Este documento descreve a arquitetura e o fluxo de funcionamento de uma Pokédex implementada como um **Portlet MVC no Liferay DXP**, explicando claramente a relação entre **JSP**, **MVCActionCommand**, **MVCRenderCommand** e a classe utilitária **PokedexClient**.

---
Para rodar

`docker run -it -m 8g -p 8080:8080 liferay/portal:7.4.3.132-ga132`

`./gradlew deploy -Ddeploy.docker.container.id=$(docker ps -lq)`

## Visão Geral da Arquitetura

A aplicação segue o padrão **MVC do Liferay**, que separa responsabilidades da seguinte forma:

* **JSP (View)**: Interface do usuário, responsável por exibir o formulário e os dados do Pokémon
* **MVCActionCommand (Controller – Action)**: Processa ações do usuário (submit do formulário)
* **MVCRenderCommand (Controller – Render)**: Prepara os dados que serão renderizados na JSP
* **PokedexClient (Service/Util)**: Comunicação com a API externa da PokéAPI

Fluxo resumido:

```
JSP (formulário)
   ↓ submit
MVCActionCommand (/pokedex/search)
   ↓ redireciona
MVCRenderCommand (/pokedex/view)
   ↓ busca dados
PokedexClient → PokéAPI
   ↓ retorna JSON
JSP (exibição)
```

---

## JSP – Interface do Usuário (`view.jsp`)

A JSP é responsável por:

* Exibir o formulário de busca
* Mostrar os dados do Pokémon retornados pelo backend
* Renderizar mensagens de erro

### Formulário de busca

```jsp
<portlet:actionURL var="searchURL" name="/pokedex/search" />

<aui:form action="<%= searchURL %>" method="post">
    <aui:input name="query" label="Nome ou ID do Pokémon" />
    <aui:button type="submit" value="Buscar" />
</aui:form>
```

Esse formulário dispara uma **Action Phase**, chamando o `MVCActionCommand`.

### Renderização dos dados

A JSP recebe os dados via `request.setAttribute()` e renderiza:

* Nome
* Imagem
* Tipos
* Altura (convertida para metros)
* Peso (convertido para quilos)
* ID, habilidades e stats (quando adicionados)

---

## MVCActionCommand – Ação de Busca (`SearchPokemonMVCActionCommand`)

Classe responsável por processar o submit do formulário.

### Principais responsabilidades

* Ler o parâmetro `query` enviado pela JSP
* Definir parâmetros de renderização
* Redirecionar para o `MVCRenderCommand`

### Código essencial

```java
String query = ParamUtil.getString(actionRequest, "query");

actionResponse.setRenderParameter("pokemonName", query);
actionResponse.setRenderParameter(
    "mvcRenderCommandName", "/pokedex/view");
```

Importante notar que **nenhuma chamada à API acontece aqui**. Essa classe apenas controla o fluxo.

---

## MVCRenderCommand – Preparação dos Dados (`PokedexViewMVCRenderCommand`)

Essa classe é executada na **Render Phase** e prepara os dados para a JSP.

### Responsabilidades

* Ler o parâmetro `pokemonName`
* Validar se o valor foi informado
* Chamar o `PokedexClient`
* Colocar os dados no `RenderRequest`

### Código principal

```java
String pokemonName = ParamUtil.getString(renderRequest, "pokemonName");

if (Validator.isNotNull(pokemonName)) {
    try {
        JsonNode pokemon = pokedexClient.getPokemonData(pokemonName);
        renderRequest.setAttribute("pokemonData", pokemon);
    } catch (Exception e) {
        renderRequest.setAttribute("errorMessage", "Pokémon não encontrado");
    }
}

return "/view.jsp";
```

Essa classe faz a ponte entre a **lógica de negócio** e a **camada de visualização**.

---

## PokedexClient – Comunicação com a PokéAPI

Classe utilitária responsável por acessar a API externa.

### Responsabilidades

* Montar a URL da requisição
* Executar a chamada HTTP
* Converter o JSON em `JsonNode`

### Código resumido

```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(API_URL + query.toLowerCase()))
    .GET()
    .build();

HttpResponse<String> response =
    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

return objectMapper.readTree(response.body());
```

A classe usa:

* `java.net.http.HttpClient`
* `Jackson ObjectMapper`

Ela não depende do Liferay, o que facilita testes e reutilização.

---

## Relação entre Actions, Render e JSP

| Camada           | Função               | Comunicação                       |
| ---------------- | -------------------- | --------------------------------- |
| JSP              | Interface do usuário | Envia dados via formulário        |
| MVCActionCommand | Processa ação        | Define parâmetros de render       |
| MVCRenderCommand | Prepara dados        | Chama o client e injeta atributos |
| JSP              | Render final         | Lê atributos do request           |

Essa separação garante:

* Código organizado
* Fácil manutenção
* Aderência ao padrão MVC do Liferay

---

## Considerações Finais

Essa implementação segue boas práticas do Liferay DXP:

* Separação clara de responsabilidades
* Uso correto das fases Action e Render
* JSP simples, sem lógica pesada
* Classe de integração desacoplada da UI

O mesmo padrão pode ser reaproveitado para outros portlets que consumam APIs externas.
