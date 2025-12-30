package com.acme.w3e7.web.internal.portlet.actions;

import com.acme.w3e7.web.internal.portlet.utils.PokedexClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

@Component(immediate = true, property = {
        "javax.portlet.name=pokedex_portlet",
        "mvc.command.name=/pokedex/view"
}, service = MVCRenderCommand.class)
public class PokedexViewMVCRenderCommand implements MVCRenderCommand {
    private final PokedexClient pokedexClient = new PokedexClient();

    @Override
    public String render(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException {
        String pokemonName = ParamUtil.getString(renderRequest, "pokemonName");

        System.out.println("CHEGOU");
        if (Validator.isNotNull(pokemonName)) {
            try {
                JsonNode pokemon = pokedexClient.getPokemonData(pokemonName);

                renderRequest.setAttribute("pokemonData", pokemon);
            } catch (Exception e) {
                renderRequest.setAttribute(
                        "errorMessage", "Pokémon não encontrado");
            }
        }
        return "/view.jsp";
    }
}
