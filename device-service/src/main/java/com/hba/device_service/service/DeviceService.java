package com.hba.device_service.service;

import com.hba.device_service.dto.DeviceDto;
import com.hba.device_service.entity.Device;
import com.hba.device_service.exception.DeviceNotFoundException;
import com.hba.device_service.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    private DeviceDto mapToDto(Device device){
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(device.getId());
        deviceDto.setName(device.getName());
        deviceDto.setLocation(device.getLocation());
        deviceDto.setType(device.getType());
        deviceDto.setUserId(device.getUserId());
        return deviceDto;
    }

    public DeviceDto getDeviceById(Long id){
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: {}" + id));
        return mapToDto(device);
    }

    public DeviceDto createDevice(DeviceDto input) {
        Device device = new Device();
        device.setName(input.getName());
        device.setLocation(input.getLocation());
        device.setType(input.getType());
        device.setUserId(input.getUserId());

        final Device savedDevice = deviceRepository.save(device);
        return mapToDto(savedDevice);
    }

    public DeviceDto updateDevice(Long id, DeviceDto input) {
        Device deviceDb = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: {}" + id));
        deviceDb.setName(input.getName());
        deviceDb.setType(input.getType());
        deviceDb.setLocation(input.getLocation());
        deviceDb.setUserId(input.getUserId());

        final Device updatedDevice = deviceRepository.save(deviceDb);
        return mapToDto(updatedDevice);
    }

    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)){
            throw new IllegalArgumentException("Device not found with id: "+ id);
        }
        deviceRepository.deleteById(id);
    }

    public List<DeviceDto> getAllDevicesByUserId(Long userId) {
        List<Device> devices = deviceRepository.findAllByUserId(userId);
        return devices.stream()
                .map(this::mapToDto)
                .toList();
    }
}
