import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {AppsRoutingModule} from './apps-routing.module';
import {AppListComponent} from './app-list/app-list.component';

@NgModule({
  declarations: [AppListComponent],
  imports: [CommonModule, AppsRoutingModule],
})
export class AppsModule {}
