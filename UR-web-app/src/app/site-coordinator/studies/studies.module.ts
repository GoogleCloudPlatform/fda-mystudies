import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StudiesRoutingModule } from './studies-routing.module';
import { StudyListComponent } from './study-list/study-list.component';


@NgModule({
  declarations: [StudyListComponent],
  imports: [
    CommonModule,
    StudiesRoutingModule
  ]
})
export class StudiesModule { }
