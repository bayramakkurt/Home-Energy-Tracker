package com.hba.usage_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    Long id;
    String name;
    String type;
    String location;
    Long userId;
    Double energyConsumed;
}
