package com.ftn.pki.dtos.certificates;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DownloadRequest {
    private UUID certificateId;
    private String password;
    private String alias;
}
