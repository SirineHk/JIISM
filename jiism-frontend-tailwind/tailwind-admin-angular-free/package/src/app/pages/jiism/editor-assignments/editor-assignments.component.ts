import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { JiismService } from '../../../services/jiism.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-editor-assignments',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './editor-assignments.component.html'
})
export class EditorAssignmentsComponent {

  private service = inject(JiismService);
  private authService = inject(AuthService);

  items: any[] = [];

  loading = true;
  error = '';

  username = '';

  stats = {
    total: 0,
    assigned: 0,
    review: 0,
    completed: 0
  };

  constructor() {
    this.username = this.authService.getUsername() || '';
    this.loadAssignments();
  }

  private loadAssignments(): void {

    this.loading = true;
    this.error = '';

    this.service.getAssignments().subscribe({

      next: (data) => {

        const allAssignments = data ?? [];

        /*
         * On garde uniquement les assignations
         * de l'éditeur actuellement connecté.
         */
        this.items = allAssignments.filter(
          assignment =>
            assignment.editor?.username === this.username
        );

        this.computeStats();

        this.loading = false;

        console.log(
          'Utilisateur connecté :',
          this.username
        );

        console.log(
          'Mes assignations :',
          this.items
        );
      },

      error: (err) => {

        console.error(
          'Erreur récupération de mes assignations :',
          err
        );

        this.items = [];

        this.computeStats();

        this.error =
          'Impossible de charger vos assignations.';

        this.loading = false;
      }
    });
  }

  private computeStats(): void {

    this.stats.total = this.items.length;

    this.stats.assigned =
      this.items.filter(
        item => item.status === 'ASSIGNED'
      ).length;

    this.stats.review =
      this.items.filter(
        item => item.status === 'IN_REVIEW'
      ).length;

    this.stats.completed =
      this.items.filter(
        item => item.status === 'COMPLETED'
      ).length;
  }

  label(status: string): string {

    const labels: Record<string, string> = {

      ASSIGNED: 'À traiter',

      IN_REVIEW: 'En révision',

      COMPLETED: 'Terminé'

    };

    return labels[status] || status;
  }
}
