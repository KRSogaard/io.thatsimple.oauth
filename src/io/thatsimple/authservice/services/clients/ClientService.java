package io.thatsimple.authservice.services.clients;

import io.thatsimple.authservice.models.exceptions.ClientNotFoundException;
import io.thatsimple.authservice.services.clients.models.Client;

public interface ClientService {
    Client getClient(String clientId) throws ClientNotFoundException;
}
