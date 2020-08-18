import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {StudiesRoutingModule} from './studies-routing.module';
import {StudyListComponent} from './study-list/study-list.component';
import {StudyDetailsComponent} from './study-details/study-details.component';
import {NgxDataTableModule} from 'angular-9-datatable';
import {FormsModule} from '@angular/forms';
import {UpdateTargetComponent} from './update-target/update-target.component';

@NgModule({
  declarations: [
    StudyListComponent,
    StudyDetailsComponent,
    UpdateTargetComponent,
  ],
  imports: [
    CommonModule,
    StudiesRoutingModule,
    NgxDataTableModule,
    FormsModule,
  ],
})
export class StudiesModule {}
