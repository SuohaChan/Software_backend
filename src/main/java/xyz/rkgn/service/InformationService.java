package xyz.rkgn.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.rkgn.common.Result;
import xyz.rkgn.dto.AddInformationDto;
import xyz.rkgn.dto.InformationQueryDto;
import xyz.rkgn.entity.Information;

public interface InformationService {

    Result deleteInformationById(Long id);

    Result page(InformationQueryDto queryDTO, Integer pageNum, Integer pageSize);

    Result updateInformation(Information information);

    Result addInformation(AddInformationDto addInformationDto, MultipartFile[] images);

    Result getInformationByUserId(Long id);

    Result getInformationById(Long id);
}
