import {NgModule} from '@angular/core';
import {MyDefaultSorterComponent} from './myDefaultSorterComponent';
import {CommonModule} from '@angular/common';
@NgModule({
  declarations: [MyDefaultSorterComponent],
  imports: [CommonModule],
  exports: [MyDefaultSorterComponent, CommonModule],
})
export class CommonComponentsModule {}
