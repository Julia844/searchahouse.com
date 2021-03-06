package edu.searchahouse.endpoints;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.searchahouse.SearchahouseApplication;
import edu.searchahouse.model.Address;
import edu.searchahouse.model.Agent;
import edu.searchahouse.model.Lead;
import edu.searchahouse.model.Property;
import edu.searchahouse.model.Property.PropertyStatus;
import edu.searchahouse.model.Property.PropertyType;
import edu.searchahouse.repository.mongo.AgentRepository;
import edu.searchahouse.repository.mongo.LeadRepository;
import edu.searchahouse.repository.mongo.PropertyRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchahouseApplication.class)
@ActiveProfiles(profiles = { "integrationTest" })
@WebIntegrationTest("server.port:0")
public class AbstractRestEndpointTest {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected PropertyRepository propertyRepository;

    @Autowired
    protected LeadRepository leadRepository;

    @Autowired
    protected AgentRepository agentRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected void createPropertiesForTest() {
        this.propertyRepository.deleteAll();

        Arrays.asList("1,2".split(",")).forEach(
                index -> {
                    Property property = new Property("Property" + index, "description" + index, new Address("CA", "test city", "test street"), 100000L, PropertyType.SALE,
                            PropertyStatus.AVAILABLE);
                    
                    property.setPrimaryKey(UUID.randomUUID().toString());
                    
                    this.propertyRepository.save(property);
                });
    }

    protected void createLeadsForTest() {
        leadRepository.deleteAll();

        Arrays.asList("1,2".split(",")).forEach(index -> {
            Lead lead = new Lead("Lead" + index, "last name " + index, index + "lead@example.com", "012345678" + index);

            lead.setPrimaryKey(UUID.randomUUID().toString());
            
            leadRepository.save(lead);
        });
    }

    protected void createAgentsForTest() {
        agentRepository.deleteAll();

        Arrays.asList("1,2".split(",")).forEach(index -> {
            Agent agent = new Agent("Agent" + index, "last name" + index, index + "agent@example.com");
            
            agent.setPrimaryKey(UUID.randomUUID().toString());

            agentRepository.save(agent);
        });
    }
    
}
