<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ page import="com.fasterxml.jackson.databind.JsonNode" %>

<portlet:actionURL
    var="searchURL"
    name="/pokedex/search" />

<aui:form action="<%= searchURL %>" method="post">
    <aui:input name="query" label="Name or Pokedex Position" />
    <aui:button type="submit" value="Buscar" />
</aui:form>

<hr/>

<%
JsonNode pokemonData = (JsonNode) request.getAttribute("pokemonData");
String errorMessage = (String) request.getAttribute("errorMessage");
%>

<% if (errorMessage != null) { %>
    <div class="alert alert-danger"><%= errorMessage %></div>
<% } %>

<% if (pokemonData != null) {
    String name = pokemonData.get("name").asText();
    String imgUrl = pokemonData.get("sprites").get("front_default").asText();
    JsonNode types = pokemonData.get("types");
    StringBuilder typeList = new StringBuilder();
    for (int i = 0; i < types.size(); i++) {
        typeList.append(types.get(i).get("type").get("name").asText()).append(" ");
    }
%>
<div class="poke-infos">
    <h3><%= name %></h3>
    <p><strong>#:</strong> <%= pokemonData.get("id").asInt() %></p>
    <div class="poke-image">
        <img src="<%= imgUrl %>" alt="Pokemon Image" />
    </div>
    <p><strong>Types:</strong> <%= typeList %></p>
    <%
    double heightMeters = pokemonData.get("height").asInt() / 10.0;
    double weightKg = pokemonData.get("weight").asInt() / 10.0;
    %>

    <p><strong>Height:</strong> <%= heightMeters %> m</p>
    <p><strong>Weight:</strong> <%= weightKg %> kg</p>

    <h4>Stats</h4>
    <ul>
    <%
    JsonNode stats = pokemonData.get("stats");

    for (int i = 0; i < stats.size(); i++) {
        String statName =
            stats.get(i).get("stat").get("name").asText();
        int statValue =
            stats.get(i).get("base_stat").asInt();
    %>
        <li>
            <strong><%= statName %>:</strong> <%= statValue %>
        </li>
    <%
    }
    %>
    </ul>

    <h4>Abilities</h4>
    <ul>
        <%
        JsonNode abilities = pokemonData.get("abilities");

        for (int i = 0; i < abilities.size(); i++) {
            String abilityName =
                abilities.get(i).get("ability").get("name").asText();
            boolean hidden =
                abilities.get(i).get("is_hidden").asBoolean();
        %>
            <li>
                <%= abilityName %>
                <% if (hidden) { %>
                    <em>(hidden)</em>
                <% } %>
            </li>
        <%
        }
        %>
    </ul>
</div>
<% } %>
