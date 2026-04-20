package com.wintersports.services.result;

import com.wintersports.dtos.requests.CreateSlalomResultRequest;
import com.wintersports.dtos.requests.UpdateSlalomResultRequest;
import com.wintersports.dtos.responses.SlalomResultResponse;

import java.util.List;

public interface ISlalomResultService {
    SlalomResultResponse create(CreateSlalomResultRequest request);

    SlalomResultResponse update(Long id, UpdateSlalomResultRequest request);

    List<SlalomResultResponse> getRun2Qualifiers(Long competitionId);
}
