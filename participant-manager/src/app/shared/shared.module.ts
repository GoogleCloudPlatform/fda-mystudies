import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FooterComponent} from '../footer/footer.component';
import {RouterModule} from '@angular/router';

@NgModule({
  declarations: [FooterComponent],
  imports: [CommonModule, RouterModule],
  exports: [FooterComponent],
})
export class SharedModule {}
