import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NgxDataTableModule} from 'angular-9-datatable';

import {AppsRoutingModule} from './apps-routing.module';
import {AppListComponent} from './app-list/app-list.component';
import {AppDetailsComponent} from './app-details/app-details.component';
import {NgxPaginationModule} from 'ngx-pagination';

@NgModule({
  declarations: [AppListComponent, AppDetailsComponent],
  imports: [
    CommonModule,
    AppsRoutingModule,
    NgxDataTableModule,
    NgxPaginationModule,
  ],
})
export class AppsModule {}
