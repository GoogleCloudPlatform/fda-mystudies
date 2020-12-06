<!--
 Copyright 2020 Google LLC
 Use of this source code is governed by an MIT-style
 license that can be found in the LICENSE file or at
 https://opensource.org/licenses/MIT.
-->

The `Common modules` directory includes common code and tests shared by all Spring boot applications. When deploying the **FDA MyStudies** platform, Jib will detect and fetch the content of `common-module/` from pom.xml to create the final service container.
