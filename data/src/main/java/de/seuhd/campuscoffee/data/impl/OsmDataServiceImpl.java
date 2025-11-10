package de.seuhd.campuscoffee.data.impl;

import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * OSM import service.
 */
@Service
@Slf4j
class OsmDataServiceImpl implements OsmDataService {

    @Override
    public @NonNull OsmNode fetchNode(@NonNull Long nodeId) throws OsmNodeNotFoundException {
        log.info("Fetching OSM node {} from OpenStreetMap API...", nodeId);
        String url = "https://www.openstreetmap.org/api/0.6/node/" + nodeId;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/xml")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int status = response.statusCode();
            if (status == 404) {
                throw new OsmNodeNotFoundException(nodeId);
            }
            if (status < 200 || status >= 300) {
                log.error("OSM API returned HTTP {} for node {}", status, nodeId);
                throw new OsmNodeNotFoundException(nodeId);
            }

            try (InputStream body = response.body()) {
                return parseOsmNodeXml(nodeId, body);
            }
        } catch (OsmNodeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch or parse OSM node {}: {}", nodeId, e.getMessage());
            throw new OsmNodeNotFoundException(nodeId);
        }
    }

    @SneakyThrows
    private @NonNull OsmNode parseOsmNodeXml(@NonNull Long nodeId, InputStream xmlStream) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        Document doc = dbf.newDocumentBuilder().parse(xmlStream);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("node");
        if (nodeList.getLength() == 0) {
            throw new OsmNodeNotFoundException(nodeId);
        }
        Element nodeEl = (Element) nodeList.item(0);

        String name = null;
        String amenity = null;
        String shop = null;
        String street = null;
        String houseNumber = null;
        Integer postalCode = null;
        String city = null;

        NodeList tagList = nodeEl.getElementsByTagName("tag");
        for (int i = 0; i < tagList.getLength(); i++) {
            Element tag = (Element) tagList.item(i);
            String k = tag.getAttribute("k");
            String v = tag.getAttribute("v");
            if (k == null) continue;
            switch (k) {
                case "name" -> name = v;
                case "amenity" -> amenity = v;
                case "shop" -> shop = v;
                case "addr:street" -> street = v;
                case "addr:housenumber" -> houseNumber = v;
                case "addr:postcode" -> {
                    try {
                        postalCode = Integer.parseInt(v);
                    } catch (NumberFormatException ignored) {
                        postalCode = null;
                    }
                }
                case "addr:city" -> city = v;
                default -> {
                    // ignore other tags
                }
            }
        }

        return OsmNode.builder()
                .nodeId(nodeId)
                .name(name)
                .amenity(amenity)
                .shop(shop)
                .street(street)
                .houseNumber(houseNumber)
                .postalCode(postalCode)
                .city(city)
                .build();
    }
}
