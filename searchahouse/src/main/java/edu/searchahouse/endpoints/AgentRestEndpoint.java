package edu.searchahouse.endpoints;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.searchahouse.endpoints.resources.assemblers.AgentResourceAssembler;
import edu.searchahouse.model.Agent;
import edu.searchahouse.model.Lead;
import edu.searchahouse.model.Property;
import edu.searchahouse.service.AgentService;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentRestEndpoint {

	// *************************************************************//
	// *********************** PROPERTIES **************************//
	// *************************************************************//
	private final AgentService agentService;

	private final AgentResourceAssembler agentResourceAssembler;

	// *************************************************************//
	// *********************** CONSTRUCTORS ************************//
	// *************************************************************//
	@Autowired
	public AgentRestEndpoint(//
			AgentService agentService,//
			AgentResourceAssembler agentResourceAssembler//
	) {
		this.agentService = agentService;
		this.agentResourceAssembler = agentResourceAssembler;
	}

	// *************************************************************//
	// ********************* REST ENDPOINTS ************************//
	// *************************************************************//

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * GET - find "Pageable" agents
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Return a pageable list of agents.
	 * 
	 * @param pageable
	 *            the page data. Page number and page size.
	 * @param assembler
	 *            the assembler that will construct the agent resource as a pageable resource.
	 * @return A pageable list of agents in json or xml format (default to json)
	 * 
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public HttpEntity<PagedResources<ResourceSupport>> getAgentsByPage( //
			@PageableDefault(size = 10, page = 0) Pageable pageable, //
			PagedResourcesAssembler<Agent> assembler //
	) {

		Page<Agent> agents = this.agentService.getAgentsByPage(pageable);

		return new ResponseEntity<>(assembler.toResource(agents, this.agentResourceAssembler), HttpStatus.OK);

	}

	/**
	 *
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * GET - find all Agents by property (paged result).
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Find all agents by a property id (return a paged result.). Throw 404 if not found.
	 * 
	 * @param propertyId
	 * @return A list of agents in json or xml format (default to json).
	 * 
	 */
	@RequestMapping(value = "/property/{propertyId}", method = RequestMethod.GET)
	public HttpEntity<ResourceSupport> getAgentByProperty(//
			@PathVariable("propertyId") final String propertyId, //
			@PageableDefault(size = 10, page = 0) Pageable pageable, //
			PagedResourcesAssembler<Agent> assembler //
	) {

		Page<Agent> agents = this.agentService.findAgentsByPropertyId(propertyId, pageable, false);

		return new ResponseEntity<>(assembler.toResource(agents, this.agentResourceAssembler), HttpStatus.OK);
	}

	/**
	 *
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * GET - find an Agent by id
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Get an agent by it's primary key. Throw 404 if not found.
	 * 
	 * @param agentId
	 * @return An agent in json or xml format (default to json).
	 * 
	 */
	@RequestMapping(value = "/{agentId}", method = RequestMethod.GET)
	public HttpEntity<ResourceSupport> getAgent(//
			@PathVariable String agentId, //
			@RequestParam(value = "lazy", defaultValue = "true", required = false) final Boolean lazyNested //
	) {
		Agent aAgent = this.agentService.findAgentByPrimaryKey(agentId, lazyNested);

		return new ResponseEntity<ResourceSupport>(this.agentResourceAssembler.toResource(aAgent), HttpStatus.OK);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * POST - Create an agent
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Create a new agent. Throw 422 if resource already exist.
	 * 
	 * @param agentId
	 * @return 201 Created and the agent location. 422 if the agent already exist.
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public HttpEntity<?> createAgent(@Valid @RequestBody Agent input) {

		Agent agent = this.agentService.save(input);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(methodOn(AgentRestEndpoint.class, agent.getPrimaryKey()).getAgent(agent.getPrimaryKey(), false)).toUri());

		return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * PUT - Add a property to an agent
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Add a property to a agent. Throw 402 if property or agent resource does not exist.
	 * 
	 * @param agentId
	 * @return 204 if updated ok, 402 if resource does not exist or 403 in case the resource exist but could not be updated.
	 */
	@RequestMapping(value = "/{agentId}/property/{propertyId}", method = RequestMethod.PUT)
	public HttpEntity<?> addProperty(//
			@PathVariable final String agentId, //
			@PathVariable final String propertyId //
	) {

		Property property = this.agentService.addProperty(agentId, propertyId);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(methodOn(PropertyRestEndpoint.class, property.getPrimaryKey()).getProperty(property.getPrimaryKey())).toUri());

		return new ResponseEntity<>("The resource was updated ok.", httpHeaders, HttpStatus.NO_CONTENT);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * PUT - Update an agent
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Updates an existing agent. Throw 404 if resource does not exist or 403 in case the resource exist but could not be updated.
	 * 
	 * @param agentId
	 * @return 204 if updated ok, 404 if resource does not exist or 403 in case the resource exist but could not be updated.
	 */
	@RequestMapping(value = "/{agentId}", method = RequestMethod.PUT)
	public HttpEntity<?> updateAgent( //
			@RequestBody Agent input,//
			@PathVariable String agentId //
	) {

		Agent agent = this.agentService.update(agentId, input);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(methodOn(AgentRestEndpoint.class, agent.getPrimaryKey()).getAgent(agent.getPrimaryKey(), false)).toUri());

		return new ResponseEntity<>("The resource was updated ok.", httpHeaders, HttpStatus.NO_CONTENT);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * PUT - Update contact status of lead for an agent.
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Updates the contact status of a lead of an existing agent. Throw 404 if resource does not exist or 403 in case the resource exist but could not be
	 * updated.
	 * 
	 * @param agentId
	 * @return 204 if updated ok, 404 if resource does not exist or 403 in case the resource exist but could not be updated.
	 */
	@RequestMapping(value = "/{agentId}/lead/{leadId}", method = RequestMethod.PUT)
	public HttpEntity<?> updateAgent( //
			@PathVariable String agentId, //
			@PathVariable String leadId, //
			@RequestBody Lead input //
	) {

		this.agentService.updateLeadContactStatus(agentId, leadId, input);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(methodOn(AgentRestEndpoint.class, agentId).getAgent(agentId, false)).toUri());

		return new ResponseEntity<>("The resource was updated ok.", httpHeaders, HttpStatus.NO_CONTENT);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * POST - Add a lead to an agent
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Create and add a lead to the agent. Throw 422 if resource already exist.
	 * 
	 * @param agentId
	 * @return 201 Created and the agent location. 422 if the agent already exist.
	 */
	@RequestMapping(value = "/{agentId}/lead", method = RequestMethod.POST)
	public HttpEntity<?> addLeadToAgent(//
			@PathVariable("agentId") final String agentId, //
			@Valid @RequestBody Lead input //
	) {

		Agent agent = this.agentService.addLead(agentId, input);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(linkTo(methodOn(AgentRestEndpoint.class, agent.getPrimaryKey()).getAgent(agent.getPrimaryKey(), false)).toUri());

		return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
	}

	/**
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * DELETE - Delete an agent
	 * 
	 * ----------------------------------------------------------------------------------------------------------------
	 * 
	 * Delete an agent. Throw 404 if resource does not exist.
	 * 
	 * @param agentId
	 * @return 200 OK. 404 if resource does not exist and 500 for any other exception (we should probably add a more specific error response)
	 */
	@RequestMapping(value = "/{agentId}", method = RequestMethod.DELETE)
	public HttpEntity<?> deleteAgent(//
			@PathVariable("agentId") final String agentId //
	) {
		this.agentService.deleteAgent(agentId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
