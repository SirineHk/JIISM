import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-assignments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assignments.component.html'
})
export class AssignmentsComponent {

  service = inject(JiismService);

  items: any[] = [];
  articles: any[] = [];
  users: any[] = [];

  editors: any[] = [];

  selectedEditor: Record<number, number | null> = {};
  dueDates: Record<number, string> = {};

  assigning: Record<number, boolean> = {};

  constructor() {
    this.loadData();
  }

  loadData() {

    this.service.getAssignments().subscribe({
      next: data => {
        this.items = data;
      },
      error: error => {
        console.error('Erreur récupération assignations :', error);
      }
    });

    this.service.getArticles().subscribe({
      next: data => {
        this.articles = data;
      },
      error: error => {
        console.error('Erreur récupération articles :', error);
      }
    });

    this.service.getUsers().subscribe({
      next: data => {
        this.users = data;

        this.editors = data.filter(
          user => user.roles?.includes('EDITEUR')
        );
      },
      error: error => {
        console.error('Erreur récupération utilisateurs :', error);
      }
    });
  }

  get pendingArticles() {

    const assignedArticleIds = this.items.map(
      assignment => assignment.article?.id
    );

    return this.articles.filter(
      article =>
        article.status === 'PENDING' &&
        !assignedArticleIds.includes(article.id)
    );
  }

  get pendingCount() {
    return this.pendingArticles.length;
  }

  get reviewCount() {
    return this.items.filter(
      x => x.status === 'IN_REVIEW'
    ).length;
  }

  get completedCount() {
    return this.items.filter(
      x => x.status === 'COMPLETED'
    ).length;
  }

  label(status: string) {
    return ({
      ASSIGNED: 'Assigné',
      IN_REVIEW: 'En révision',
      COMPLETED: 'Terminé'
    } as any)[status] || status;
  }

  assignArticle(article: any) {

    const editorId = this.selectedEditor[article.id];
    const dueDate = this.dueDates[article.id];

    if (!editorId) {
      alert('Veuillez sélectionner un éditeur.');
      return;
    }

    if (!dueDate) {
      alert('Veuillez sélectionner une date d’échéance.');
      return;
    }

    this.assigning[article.id] = true;

    const dateTime = `${dueDate}T23:59:59`;

    this.service
      .assignArticle(article.id, editorId, dateTime)
      .subscribe({
        next: () => {

          alert('Article assigné avec succès.');

          this.assigning[article.id] = false;

          this.loadData();
        },

        error: error => {

          console.error('Erreur assignation article :', error);

          this.assigning[article.id] = false;

          alert(
            error?.error?.message ||
            
            'Impossible d’assigner l’article.'
          );
        }
      });
  }
}