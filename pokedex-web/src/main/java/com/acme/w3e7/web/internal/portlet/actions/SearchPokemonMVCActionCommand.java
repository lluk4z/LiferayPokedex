package com.acme.w3e7.web.internal.portlet.actions;

import com.acme.w3e7.web.internal.portlet.utils.PokedexClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

@Component(immediate = true, property = {
                "javax.portlet.name=pokedex_portlet",
                "mvc.command.name=/pokedex/search",
                "csrf.protection.enabled=false"
}, service = MVCActionCommand.class)
public class SearchPokemonMVCActionCommand extends BaseMVCActionCommand {

        private PokedexClient pokedexClient = new PokedexClient();

        @Override
        protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
                String query = ParamUtil.getString(actionRequest, "query");

                actionResponse.setRenderParameter("pokemonName", query);
                actionResponse.setRenderParameter(
                                "mvcRenderCommandName", "/pokedex/view");
        }
}
