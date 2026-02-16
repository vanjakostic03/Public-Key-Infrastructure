package com.ftn.pki.dtos.certificates;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadResponseDTO {
    String fileName;
    private byte[] certificateBytes;
}
