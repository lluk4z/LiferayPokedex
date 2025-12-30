package com.acme.w3e7.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import org.osgi.service.component.annotations.Component;

import javax.portlet.Portlet;

@Component(property = {
		"com.liferay.portlet.display-category=category.sample",
		"javax.portlet.display-name=Pokedex Portlet",
		"javax.portlet.name=pokedex_portlet",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"javax.portlet.init-param.view-template=/view.jsp"
}, service = Portlet.class)
public class PokedexPortlet extends MVCPortlet {
}