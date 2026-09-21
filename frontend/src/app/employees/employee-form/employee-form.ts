import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { EmployeeService } from '../employee.service';

@Component({
  selector: 'app-employee-form',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './employee-form.html',
  styleUrl: './employee-form.scss',
})
export class EmployeeForm {
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);

  readonly saving = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = new FormGroup({
    firstName: new FormControl('', { nonNullable: true, validators: Validators.required }),
    lastName: new FormControl('', { nonNullable: true, validators: Validators.required }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    department: new FormControl('', { nonNullable: true, validators: Validators.required }),
    jobTitle: new FormControl('', { nonNullable: true, validators: Validators.required }),
    country: new FormControl('', { nonNullable: true, validators: Validators.required }),
    currency: new FormControl('', { nonNullable: true, validators: Validators.required }),
    dateOfJoining: new FormControl('', { nonNullable: true, validators: Validators.required }),
    basic: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    allowances: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    deductions: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.employeeService.create(this.form.getRawValue()).subscribe({
      next: (employee) => this.router.navigate(['/employees', employee.id]),
      error: () => {
        this.saving.set(false);
        this.error.set('Could not create the employee. Please check the details and try again.');
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/']);
  }
}
