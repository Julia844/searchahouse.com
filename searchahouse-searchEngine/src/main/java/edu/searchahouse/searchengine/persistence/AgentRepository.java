package edu.searchahouse.searchengine.persistence;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import edu.searchahouse.searchengine.model.Agent;

public interface AgentRepository extends ElasticsearchRepository<Agent, String> {

	@Query(name = "autocomplete_firstName", value = "{\"match\" : { \"firstName.autocomplete\" : { \"query\" : \"gus\", \"analyzer\" : \"standard\" } } }")
	public List<Agent> findAutocompleteAgentsByFirstName(final String autocompleteFirstName);

}
