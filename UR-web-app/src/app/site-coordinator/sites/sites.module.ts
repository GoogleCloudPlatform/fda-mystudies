import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {SitesRoutingModule} from './sites-routing.module';
import {SiteListComponent} from './site-list/site-list.component';

@NgModule({
  declarations: [SiteListComponent],
  imports: [CommonModule, SitesRoutingModule, FormsModule],
})
export class SitesModule {}
