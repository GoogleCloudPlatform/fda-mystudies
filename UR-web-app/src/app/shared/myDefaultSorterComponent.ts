import {Component, Input, OnInit} from '@angular/core';
import {SortEvent, DataTable} from 'angular-9-datatable';

@Component({
  selector: 'mySorter',
  template: `<a style="cursor: pointer" (click)="sort()" class="text-nowrap">
    <ng-content></ng-content>
    <span
      *ngIf="isSortedByMeAsc"
      class="glyphicon glyphicon-triangle-top"
      aria-hidden="true"
    ></span>
    <i
      *ngIf="!isSortedByMeAsc && !isSortedByMeDesc"
      class="fa fa-sort-desc"
      aria-hidden="true"
    ></i>
    <span
      *ngIf="isSortedByMeDesc"
      class="glyphicon glyphicon-triangle-bottom"
      aria-hidden="true"
    ></span>
    <span
      *ngIf="!isSortedByMeAsc && !isSortedByMeDesc"
      class="glyphicon glyphicon-sort"
      aria-hidden="true"
    ></span>
    <i *ngIf="isSortedByMeAsc" class="fa fa-sort-asc" aria-hidden="true"></i>
  </a>`,
})
export class MyDefaultSorterComponent implements OnInit {
  @Input('by') sortBy = '';
  isSortedByMeAsc = false;
  isSortedByMeDesc = false;

  constructor(private readonly mfTable: DataTable) {}

  public ngOnInit(): void {
    this.mfTable.onSortChange.subscribe((event: SortEvent) => {
      this.isSortedByMeAsc =
        event.sortBy === this.sortBy && event.sortOrder === 'asc';
      this.isSortedByMeDesc =
        event.sortBy === this.sortBy && event.sortOrder === 'desc';
    });
  }

  sort(): void {
    if (this.isSortedByMeAsc) {
      this.mfTable.setSort(this.sortBy, 'desc');
    } else {
      this.mfTable.setSort(this.sortBy, 'asc');
    }
  }
}
