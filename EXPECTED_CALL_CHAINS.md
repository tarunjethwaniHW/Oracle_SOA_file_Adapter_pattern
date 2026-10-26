# Expected Call Chains - SOA File Adapter Test

This document describes the **expected linking results** when CodeScout parses this test project.

---

## Visual Representation

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                           FileProcessingComposite                                       │
│                                                                                         │
│  ENTRY POINT 1: SOAP API                                                               │
│  ========================                                                               │
│                                                                                         │
│  ┌──────────────────────┐    ┌───────────────────────┐    ┌────────────────────────┐  │
│  │ FileProcessingClient │    │ FileProcessingService │    │  FileProcessingProcess │  │
│  │      (Java)          │───▶│       (WSDL)          │───▶│      (SOA_SERVICE)     │  │
│  │                      │    │                       │    │                        │  │
│  └──────────────────────┘    └───────────────────────┘    └───────────┬────────────┘  │
│                                                                       │               │
│                                                                       ▼               │
│  ENTRY POINT 2: FILE ADAPTER                           ┌────────────────────────────┐ │
│  ===========================                           │   FileProcessingProcess    │ │
│                                                        │     (SOA_COMPONENT)        │ │
│  ┌──────────────────────┐    ┌───────────────────┐    │                            │ │
│  │ /data/incoming/*.xml │    │ FileAdapterInbound│    └───────────┬────────────────┘ │
│  │ (File System)        │───▶│ (JCA_INBOUND)     │                │                  │
│  │                      │    │ + SOA_SERVICE     │────────────────┘                  │
│  └──────────────────────┘    └───────────────────┘                                   │
│                                                                       │               │
│                                                                       ▼               │
│                                                        ┌────────────────────────────┐ │
│                                                        │   FileProcessingProcess    │ │
│                                                        │       (BPEL_ORCH)          │ │
│                                                        └───────────┬────────────────┘ │
│                                                                    │                  │
│                                                                    ▼                  │
│                                                        ┌────────────────────────────┐ │
│                                                        │   FileAdapterOutbound      │ │
│                                                        │     (SOA_REFERENCE)        │ │
│                                                        └───────────┬────────────────┘ │
│                                                                    │                  │
│                                                                    ▼                  │
│  ┌──────────────────────┐    ┌───────────────────┐                                   │
│  │ /data/outgoing/*.xml │◀───│ FileAdapterOutbound│◀──────────────────────────────────│
│  │ (File System)        │    │ (JCA_OUTBOUND)    │                                   │
│  └──────────────────────┘    └───────────────────┘                                   │
│                                                                                         │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Expected Snippets (Parser Phase)

### From JCA Files

| Source File | type_of_snippet | function_name | class_name | FileMetadata |
|-------------|-----------------|---------------|------------|--------------|
| `FileAdapter_Inbound.jca` | `JCA_ADAPTER_INBOUND` | `Read` | `FileAdapterInbound` | `{direction: "INBOUND", portType: "Read_ptt", physicalDirectory: "/data/incoming", ...}` |
| `FileAdapter_Outbound.jca` | `JCA_ADAPTER_OUTBOUND` | `Write` | `FileAdapterOutbound` | `{direction: "OUTBOUND", portType: "Write_ptt", physicalDirectory: "/data/outgoing", ...}` |

### From composite.xml

| Element | type_of_snippet | function_name | class_name |
|---------|-----------------|---------------|------------|
| `<service name="FileProcessingService">` | `SOA_SERVICE` | `FileProcessingPortType` | `FileProcessingComposite` |
| `<service name="FileAdapterInbound">` | `SOA_SERVICE` | `Read_ptt` | `FileProcessingComposite` |
| `<component name="FileProcessingProcess">` | `SOA_COMPONENT` | `FileProcessingProcess` | `FileProcessingComposite` |
| `<reference name="FileAdapterOutbound">` | `SOA_REFERENCE` | `FileAdapterOutbound` | `FileProcessingComposite` |

### From WSDL Files

| Source File | type_of_snippet | function_name | class_name |
|-------------|-----------------|---------------|------------|
| `FileProcessingService.wsdl` | `WSDL_OPERATION` | `processFile` | `FileProcessingPortType` |
| `FileAdapter_Inbound.wsdl` | `WSDL_OPERATION` | `Read` | `Read_ptt` |
| `FileAdapter_Outbound.wsdl` | `WSDL_OPERATION` | `Write` | `Write_ptt` |

### From BPEL File

| Source File | type_of_snippet | function_name | class_name |
|-------------|-----------------|---------------|------------|
| `FileProcessingProcess.bpel` | `BPEL_ORCH` | `FileProcessingProcess` | `FileProcessingProcess.bpel` |

---

## Expected Callees (Parser Phase)

### From JCA Inbound
```
JCA_ADAPTER_INBOUND(Read) --HAS_CALLEE--> Callee {
    callee_type: "JCA_TO_WSDL",
    function_name: "Read",
    function_class: "Read_ptt"
}

JCA_ADAPTER_INBOUND(Read) --HAS_CALLEE--> Callee {
    callee_type: "JCA_TO_SERVICE",  // Only for INBOUND
    function_name: "Read_ptt",
    function_class: "SOA_SERVICE"
}
```

### From composite.xml Services
```
SOA_SERVICE(FileProcessingService) --HAS_CALLEE--> Callee {
    callee_type: "SERVICE_TO_COMPONENT",
    function_name: "FileProcessingProcess",
    function_class: "FileProcessingComposite"
}

SOA_SERVICE(FileAdapterInbound) --HAS_CALLEE--> Callee {
    callee_type: "SERVICE_TO_COMPONENT",
    function_name: "FileProcessingProcess",
    function_class: "FileProcessingComposite"
}
```

### From composite.xml Component
```
SOA_COMPONENT(FileProcessingProcess) --HAS_CALLEE--> Callee {
    callee_type: "COMPONENT_TO_BPEL",
    function_name: "FileProcessingProcess",
    callee_file_name: "FileProcessingProcess.bpel"
}
```

### From BPEL
```
BPEL_ORCH(FileProcessingProcess) --HAS_CALLEE--> Callee {
    callee_type: "BPEL_TO_REFERENCE",
    function_name: "FileAdapterOutbound",
    function_class: "SOA_REFERENCE"
}
```

### From composite.xml Reference
```
SOA_REFERENCE(FileAdapterOutbound) --HAS_CALLEE--> Callee {
    callee_type: "REFERENCE_TO_JCA",
    function_name: "FileAdapterOutbound",
    callee_file_name: "FileAdapter_Outbound.jca"
}
```

---

## Expected CALLS Relationships (Linker Phase)

### Chain 1: File Adapter Inbound Entry

```cypher
// 1. JCA_INBOUND → SOA_SERVICE
(JCA_ADAPTER_INBOUND {function_name: "Read"})
    -[:CALLS {type_of_construct: "JCA_TO_SERVICE"}]->
    (SOA_SERVICE {function_name: "Read_ptt"})

// 2. SOA_SERVICE → SOA_COMPONENT
(SOA_SERVICE {function_name: "Read_ptt"})
    -[:CALLS {type_of_construct: "SERVICE_TO_COMPONENT"}]->
    (SOA_COMPONENT {function_name: "FileProcessingProcess"})

// 3. SOA_COMPONENT → BPEL
(SOA_COMPONENT {function_name: "FileProcessingProcess"})
    -[:CALLS {type_of_construct: "COMPONENT_TO_BPEL"}]->
    (BPEL_ORCH {function_name: "FileProcessingProcess"})

// 4. BPEL → SOA_REFERENCE
(BPEL_ORCH {function_name: "FileProcessingProcess"})
    -[:CALLS {type_of_construct: "BPEL_TO_REFERENCE"}]->
    (SOA_REFERENCE {function_name: "FileAdapterOutbound"})

// 5. SOA_REFERENCE → JCA_OUTBOUND
(SOA_REFERENCE {function_name: "FileAdapterOutbound"})
    -[:CALLS {type_of_construct: "REFERENCE_TO_JCA"}]->
    (JCA_ADAPTER_OUTBOUND {function_name: "Write"})
```

### Chain 2: SOAP API Entry

```cypher
// 1. WSDL_OPERATION → SOA_SERVICE
(WSDL_OPERATION {function_name: "processFile"})
    -[:CALLS {type_of_construct: "WSDL_TO_SOA_SERVICE"}]->
    (SOA_SERVICE {function_name: "FileProcessingPortType"})

// 2. SOA_SERVICE → SOA_COMPONENT
(SOA_SERVICE {function_name: "FileProcessingPortType"})
    -[:CALLS {type_of_construct: "SERVICE_TO_COMPONENT"}]->
    (SOA_COMPONENT {function_name: "FileProcessingProcess"})

// 3-5. Same as Chain 1 from SOA_COMPONENT onwards
```

---

## File Adapter Metadata to Capture

### Currently Captured (JcaParser.cs)
- `adapterName`
- `adapterType`
- `portType`
- `operation`
- `wsdlLocation`
- `connectionFactory`
- `direction` (INBOUND/OUTBOUND)
- `jmsQueueJndi` (for JMS adapters)

### Enhancement Needed for File Adapters
- `physicalDirectory`
- `logicalDirectory`
- `physicalArchiveDirectory`
- `includeFiles`
- `excludeFiles`
- `pollingFrequency`
- `minimumAge`
- `deleteFile`
- `fileNamingConvention`
- `append`
- `encoding`

---

## Cypher Query to Validate Complete Chain

```cypher
// Verify File Adapter Inbound → Outbound chain exists
MATCH path = (jca_in:Snippet {type_of_snippet: 'JCA_ADAPTER_INBOUND'})
    -[:CALLS*1..6]->
    (jca_out:Snippet {type_of_snippet: 'JCA_ADAPTER_OUTBOUND'})
WHERE jca_in.project_id = $project_id
  AND jca_in.run_id = $run_id
RETURN path, length(path) as chain_length
```
