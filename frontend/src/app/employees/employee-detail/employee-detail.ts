import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';

import { EmployeeService } from '../employee.service';
import { Employee } from '../employee.model';

@Component({
  selector: 'app-employee-detail',
  imports: [
    DecimalPipe,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
  ],
  templateUrl: './employee-detail.html',
  styleUrl: './employee-detail.scss',
})
export class EmployeeDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);

  readonly employee = signal<Employee | null>(null);
  readonly loading = signal(false);
  readonly saving = signal(false);

  readonly basic = signal(0);
  readonly allowances = signal(0);
  readonly deductions = signal(0);

  readonly previewGross = computed(() => this.basic() + this.allowances());
  readonly previewNet = computed(() => this.previewGross() - this.deductions());

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.load(id);
  }

  private load(id: number): void {
    this.loading.set(true);
    this.employeeService.getById(id).subscribe((employee) => {
      this.applyEmployee(employee);
      this.loading.set(false);
    });
  }

  private applyEmployee(employee: Employee): void {
    this.employee.set(employee);
    this.basic.set(employee.basicAmount);
    this.allowances.set(employee.allowancesAmount);
    this.deductions.set(employee.deductionsAmount);
  }

  saveSalary(): void {
    const employee = this.employee();
    if (!employee) {
      return;
    }
    this.saving.set(true);
    this.employeeService
      .updateSalary(employee.id, {
        basic: this.basic(),
        allowances: this.allowances(),
        deductions: this.deductions(),
      })
      .subscribe((updated) => {
        this.applyEmployee(updated);
        this.saving.set(false);
      });
  }

  toggleStatus(): void {
    const employee = this.employee();
    if (!employee) {
      return;
    }
    const result =
      employee.status === 'ACTIVE'
        ? this.employeeService.deactivate(employee.id)
        : this.employeeService.reactivate(employee.id);
    result.subscribe((updated) => this.employee.set(updated));
  }

  back(): void {
    this.router.navigate(['/']);
  }
}
