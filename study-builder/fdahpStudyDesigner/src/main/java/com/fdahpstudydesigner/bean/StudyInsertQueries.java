package com.fdahpstudydesigner.bean;

import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyInsertQueries {

  private StudyBo studyBo;

  private StudySequenceBo studySequenceBo;

  private AnchorDateTypeBo anchorDateTypeBo;
}
