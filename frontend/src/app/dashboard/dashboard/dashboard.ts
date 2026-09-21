import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';

import { DashboardService } from '../dashboard.service';
import { EmployeeService } from '../../employees/employee.service';
import { CurrencyAwareStats, GroupDimension, GroupedSalaryStats, MIXED_CURRENCY } from '../dashboard.model';
import { FilterOptions } from '../../employees/employee.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    DecimalPipe,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatProgressBarModule,
    MatSelectModule,
    MatTableModule,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly employeeService = inject(EmployeeService);

  readonly displayedColumns = ['group', 'count', 'average', 'median', 'min', 'max', 'currency'];

  readonly filterOptions = signal<FilterOptions>({ departments: [], countries: [], jobTitles: [] });
  readonly department = signal<string | undefined>(undefined);
  readonly country = signal<string | undefined>(undefined);
  readonly jobTitle = signal<string | undefined>(undefined);

  readonly summary = signal<CurrencyAwareStats | null>(null);
  readonly dimension = signal<GroupDimension>('DEPARTMENT');
  readonly breakdown = signal<GroupedSalaryStats[]>([]);
  readonly loading = signal(false);

  readonly isMixedCurrency = computed(() => this.summary()?.currency === MIXED_CURRENCY);

  isMixedRow(row: GroupedSalaryStats): boolean {
    return row.stats.currency === MIXED_CURRENCY;
  }

  ngOnInit(): void {
    this.employeeService.filterOptions().subscribe((options) => this.filterOptions.set(options));
    this.loadSummary();
    this.loadBreakdown();
  }

  loadSummary(): void {
    this.dashboardService
      .summary({ department: this.department(), country: this.country(), jobTitle: this.jobTitle() })
      .subscribe((stats) => this.summary.set(stats));
  }

  onFilterChange(): void {
    this.loadSummary();
  }

  onDimensionChange(dimension: GroupDimension): void {
    this.dimension.set(dimension);
    this.loadBreakdown();
  }

  private loadBreakdown(): void {
    this.loading.set(true);
    this.dashboardService.breakdown(this.dimension()).subscribe((rows) => {
      this.breakdown.set(rows);
      this.loading.set(false);
    });
  }
}
