package com.wintersports.services.result;

import com.wintersports.dtos.requests.CreateBiathlonResultRequest;
import com.wintersports.dtos.requests.UpdateBiathlonResultRequest;
import com.wintersports.dtos.responses.BiathlonResultResponse;

public interface IBiathlonResultService {
    BiathlonResultResponse create(CreateBiathlonResultRequest request);

    BiathlonResultResponse update(Long id, UpdateBiathlonResultRequest request);
}
