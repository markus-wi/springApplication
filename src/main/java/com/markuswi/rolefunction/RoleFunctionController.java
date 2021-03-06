package com.markuswi.rolefunction;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.markuswi.role.Role;
import com.markuswi.role.RoleService;

@Controller
public class RoleFunctionController {

	@Autowired
	private RoleFunctionService roleFunctionService;

	@Autowired
	private RoleService roleService;

	private static final String LIST_ROLE_FUNCTIONS_BY_ROLE_MAPPING = "/listRoleFunctions";
	private static final String LIST_ROLE_FUNCTIONS_BY_ROLE_VIEW = "roleFunction/listRoleFunctions";
	private static final String SAVE_ROLE_FUNCTIONS_MAPPING = "/saveRoleFunctions";

	@RequestMapping(method = RequestMethod.GET, value = RoleFunctionController.LIST_ROLE_FUNCTIONS_BY_ROLE_MAPPING + "/{roleId}")
	public ModelAndView listRoleFunctionsByRole(@PathVariable String roleId) {
		ModelAndView modelAndView = new ModelAndView(RoleFunctionController.LIST_ROLE_FUNCTIONS_BY_ROLE_VIEW);
		Role role = roleService.loadRoleById(roleId);
		if (role != null) {
			modelAndView.addObject("role", role);
			/***/
			List<String> navigationPoints = new LinkedList<String>();
			navigationPoints.add("Navigationsverwaltung");
			navigationPoints.add("Sprachenverwaltung");
			modelAndView.addObject("navigationPoints", navigationPoints);
		}
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = RoleFunctionController.SAVE_ROLE_FUNCTIONS_MAPPING)
	public ModelAndView saveRoleFunctions(@ModelAttribute Role role) {
		RedirectView redirectView = new RedirectView(RoleFunctionController.LIST_ROLE_FUNCTIONS_BY_ROLE_MAPPING + "/" + role.getId(), true);
		redirectView.setExposeModelAttributes(false);
		
		Role storedRole = roleService.loadRoleById(role.getId());
		if(storedRole != null) {
			storedRole.setNavigationPointId(role.getNavigationPointId());
			roleService.saveRole(storedRole);
		}
		
		for (RoleFunction roleFunction : role.getRoleFunctions()) {
			RoleFunction storedRoleFunction = roleFunctionService.loadRoleFunctionById(roleFunction.getId());
			if (storedRoleFunction != null) {
				if (storedRoleFunction.getFunction().isReadableEditable()) {
					storedRoleFunction.setReadable(roleFunction.isReadable());
				}
				if (storedRoleFunction.getFunction().isWriteableEditable()) {
					storedRoleFunction.setWriteable(roleFunction.isWriteable());
				}
				if (storedRoleFunction.getFunction().isDeleteableEditable()) {
					storedRoleFunction.setDeleteable(roleFunction.isDeleteable());
				}
				if (storedRoleFunction.getFunction().isDeactivateableEditable()) {
					storedRoleFunction.setDeactivateable(roleFunction.isDeactivateable());
				}
				roleFunctionService.saveRoleFunction(storedRoleFunction);
			}
		}
		return new ModelAndView(redirectView);
	}

}
