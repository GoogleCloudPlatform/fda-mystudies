/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

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
