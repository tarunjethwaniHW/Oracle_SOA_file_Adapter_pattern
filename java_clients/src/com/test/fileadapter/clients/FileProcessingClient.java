package com.test.fileadapter.clients;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * JAX-WS client that calls FileProcessingService SOA composite via WSDL.
 * This demonstrates the SOAP API entry path to the BPEL process.
 *
 * ================================================================
 * CALL CHAIN (SOAP API Entry):
 * ================================================================
 *
 * FileProcessingClient.java (THIS FILE)
 *         |
 *         | JAX-WS SOAP Call
 *         v
 * FileProcessingService.wsdl
 *         |
 *         | WSDL_TO_SERVICE_ENTRY
 *         v
 * composite.xml -> service "FileProcessingService" (SOA_SERVICE)
 *         |
 *         | SERVICE_TO_COMPONENT
 *         v
 * composite.xml -> component "FileProcessingProcess" (SOA_COMPONENT)
 *         |
 *         | COMPONENT_TO_BPEL
 *         v
 * FileProcessingProcess.bpel (BPEL_ORCH)
 *         |
 *         | BPEL_TO_REFERENCE
 *         v
 * composite.xml -> reference "FileAdapterOutbound" (SOA_REFERENCE)
 *         |
 *         | REFERENCE_TO_JCA
 *         v
 * FileAdapter_Outbound.jca (JCA_ADAPTER_OUTBOUND)
 *         |
 *         | Writes file
 *         v
 * /data/outgoing/soap_*.xml
 *
 * ================================================================
 * WSDL Contract: soa-composite/FileProcessingService.wsdl
 * Service: fileprocessing_client_ep
 * PortType: FileProcessingPortType
 * Operation: processFile
 * Endpoint: http://localhost:8090/soap/FileProcessingService
 * ================================================================
 */
public class FileProcessingClient {

    private static final String WSDL_URL = "http://localhost:8090/soap/FileProcessingService?wsdl";
    private static final String NAMESPACE = "http://test.com/soa/fileprocessing";
    private static final String SERVICE_NAME = "fileprocessing_client_ep";

    /**
     * Main entry point - demonstrates calling SOA Suite via WSDL.
     */
    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     FILE PROCESSING CLIENT - Calling SOA Suite via WSDL");
        System.out.println("=========================================================================");
        System.out.println("Target WSDL: " + WSDL_URL);
        System.out.println("Target Namespace: " + NAMESPACE);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("=========================================================================");

        try {
            // Create JAX-WS service proxy
            URL wsdlURL = new URL(WSDL_URL);
            QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
            Service service = Service.create(wsdlURL, serviceName);

            // Get port (interface proxy)
            // In real implementation: FileProcessingPortType port = service.getPort(FileProcessingPortType.class);

            System.out.println("\n[FileProcessingClient] Service proxy created successfully");
            System.out.println("[FileProcessingClient] Would call: processFile operation");

            // Test Case: Process a file via SOAP API
            System.out.println("\n--- TEST CASE: Process File via SOAP API ---");
            System.out.println("[Request] requestId: REQ-001");
            System.out.println("[Request] fileName: test_document.xml");
            System.out.println("[Request] processingType: TRANSFORM");

            // Simulated response
            System.out.println("\n[Response] Status: SUCCESS");
            System.out.println("[Response] Output Path: /data/outgoing/soap_REQ-001.xml");
            System.out.println("[Response] Details: File processed successfully via SOAP API entry point");

        } catch (Exception e) {
            System.err.println("[FileProcessingClient] Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
        System.out.println("     ALTERNATIVE ENTRY: File Adapter Inbound");
        System.out.println("=========================================================================");
        System.out.println("To trigger processing via File Adapter:");
        System.out.println("  1. Place a file in: /data/incoming/");
        System.out.println("  2. File patterns accepted: *.xml, *.csv, *.json");
        System.out.println("  3. Polling frequency: 15 seconds");
        System.out.println("  4. Output written to: /data/outgoing/file_*.xml");
        System.out.println("=========================================================================");
    }
}
