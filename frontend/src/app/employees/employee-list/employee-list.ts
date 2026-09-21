import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';

import { EmployeeService } from '../employee.service';
import { Employee, EmployeeStatus, FilterOptions } from '../employee.model';

@Component({
  selector: 'app-employee-list',
  imports: [
    DecimalPipe,
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatSelectModule,
    MatTableModule,
  ],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);

  readonly displayedColumns = [
    'employeeCode',
    'name',
    'department',
    'country',
    'jobTitle',
    'status',
    'grossAmount',
    'netAmount',
    'actions',
  ];

  readonly employees = signal<Employee[]>([]);
  readonly totalElements = signal(0);
  readonly loading = signal(false);
  readonly filterOptions = signal<FilterOptions>({ departments: [], countries: [], jobTitles: [] });

  readonly department = signal<string | undefined>(undefined);
  readonly country = signal<string | undefined>(undefined);
  readonly jobTitle = signal<string | undefined>(undefined);
  readonly status = signal<EmployeeStatus | undefined>(undefined);
  readonly query = signal('');
  readonly pageIndex = signal(0);
  readonly pageSize = signal(20);

  ngOnInit(): void {
    this.employeeService.filterOptions().subscribe((options) => this.filterOptions.set(options));
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.loading.set(true);
    this.employeeService
      .search({
        department: this.department(),
        country: this.country(),
        jobTitle: this.jobTitle(),
        status: this.status(),
        query: this.query() || undefined,
        page: this.pageIndex(),
        size: this.pageSize(),
      })
      .subscribe((page) => {
        this.employees.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      });
  }

  onFilterChange(): void {
    this.pageIndex.set(0);
    this.loadEmployees();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadEmployees();
  }

  viewEmployee(employee: Employee): void {
    this.router.navigate(['/employees', employee.id]);
  }

  createEmployee(): void {
    this.router.navigate(['/employees/new']);
  }

  toggleStatus(employee: Employee, event: Event): void {
    event.stopPropagation();
    const result =
      employee.status === 'ACTIVE'
        ? this.employeeService.deactivate(employee.id)
        : this.employeeService.reactivate(employee.id);
    result.subscribe(() => this.loadEmployees());
  }
}
