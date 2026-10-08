import { Injectable, inject } from '@angular/core';
import {
  HttpClient,
  HttpHeaders
} from '@angular/common/http';

import {
  Observable,
  catchError,
  throwError
} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JiismService {

  private http = inject(HttpClient);

  private base = 'http://jiism-backend-jiism.apps.dell64.dottn.com';

  // =====================================================
  // JWT
  // =====================================================

  private headers() {

    const token = localStorage.getItem('token');

    if (!token) {
      return {};
    }

    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }


  // =====================================================
  // ARTICLES
  // =====================================================

  getArticles(): Observable<any[]> {

    return this.http
      .get<any[]>(
        `${this.base}/article/articles`,
        this.headers()
      )
      .pipe(

        catchError(error => {

          console.error(
            'Erreur GET /article/articles :',
            error
          );

          return throwError(() => error);
        })

      );
  }


  // =====================================================
  // ASSIGNATIONS
  // =====================================================

  getAssignments(): Observable<any[]> {

    return this.http
      .get<any[]>(
        `${this.base}/article-assignments`,
        this.headers()
      )
      .pipe(

        catchError(error => {

          console.error(
            'Erreur GET /article-assignments :',
            error
          );

          return throwError(() => error);
        })

      );
  }


  // =====================================================
  // ANNONCES ADMIN
  // =====================================================

  getAnnouncements(): Observable<any[]> {

    return this.http
      .get<any[]>(
        `${this.base}/announcements/admin`,
        this.headers()
      )
      .pipe(

        catchError(error => {

          console.error(
            'Erreur GET /announcements/admin :',
            error
          );

          return throwError(() => error);
        })

      );
  }


  // =====================================================
  // ANNONCES PUBLIQUES
  // =====================================================

  getPublicAnnouncements(): Observable<any[]> {

    return this.http
      .get<any[]>(
        `${this.base}/announcements/public`
      )
      .pipe(

        catchError(error => {

          console.error(
            'Erreur GET /announcements/public :',
            error
          );

          return throwError(() => error);
        })

      );
  }


  getUsers(): Observable<any[]> {
  return this.http
    .get<any[]>(`${this.base}/admin/users`, this.headers())
    .pipe(
      catchError(error => {
        console.error('Erreur GET /admin/users :', error);
        return throwError(() => error);
      })
    );
}

assignArticle(
  articleId: number,
  editorId: number,
  dueDate: string
): Observable<any> {

  const params = {
    articleId: articleId.toString(),
    editorId: editorId.toString(),
    dueDate: dueDate
  };

  return this.http
    .post(
      `${this.base}/article-assignments`,
      null,
      {
        ...this.headers(),
        params
      }
    )
    .pipe(
      catchError(error => {
        console.error('Erreur POST /article-assignments :', error);
        return throwError(() => error);
      })
    );
}
createAnnouncement(data: {
  title: string;
  content: string;
}): Observable<any> {

  return this.http
    .post(
      `${this.base}/announcements/admin`,
      data,
      this.headers()
    )
    .pipe(
      catchError(error => {
        console.error(
          'Erreur POST /announcements/admin :',
          error
        );

        return throwError(() => error);
      })
    );
}


publishAnnouncement(id: number): Observable<any> {
  return this.http
    .put(
      `${this.base}/announcements/admin/${id}/publish`,
      null,
      this.headers()
    )
    .pipe(
      catchError(error => {
        console.error(
          'Erreur PUT /announcements/admin/{id}/publish :',
          error
        );
        return throwError(() => error);
      })
    );
}

unpublishAnnouncement(id: number): Observable<any> {
  return this.http
    .put(
      `${this.base}/announcements/admin/${id}/unpublish`,
      null,
      this.headers()
    )
    .pipe(
      catchError(error => {
        console.error(
          'Erreur PUT /announcements/admin/{id}/unpublish :',
          error
        );
        return throwError(() => error);
      })
    );
}

deleteAnnouncement(id: number): Observable<any> {
  return this.http
    .delete(
      `${this.base}/announcements/admin/${id}`,
      this.headers()
    )
    .pipe(
      catchError(error => {
        console.error(
          'Erreur DELETE /announcements/admin/{id} :',
          error
        );
        return throwError(() => error);
      })
    );
}
getPublicArticles(): Observable<any[]> {
  return this.http
    .get<any[]>(
      `${this.base}/public/articles`
    )
    .pipe(
      catchError(error => {
        console.error(
          'Erreur GET /public/articles :',
          error
        );
        return throwError(() => error);
      })
    );
}

startAssignment(assignmentId: number) {
  return this.http.put(
    `${this.base}/article-assignments/${assignmentId}/start`,
    {},this.headers() 
  );
}

completeAssignment(assignmentId: number) {
  return this.http.put(
    `${this.base}/article-assignments/${assignmentId}/complete`,
    {},this.headers() 
  );
}

acceptAssignment(id: number): Observable<any> {
  return this.http.put(
    `${this.base}/article-assignments/${id}/accept`,
    {},
    this.headers()
  ).pipe(
    catchError(error => {
      console.error(
        'Erreur PUT /article-assignments/{id}/accept :',
        error
      );

      return throwError(() => error);
    })
  );
}

rejectAssignment(id: number): Observable<any> {
  return this.http.put(
    `${this.base}/article-assignments/${id}/reject`,
    {},
    this.headers()
  ).pipe(
    catchError(error => {
      console.error(
        'Erreur PUT /article-assignments/{id}/reject :',
        error
      );

      return throwError(() => error);
    })
  );
}

publishArticle(id: number): Observable<any> {
  return this.http.put(
    `${this.base}/admin/articles/${id}/publish`,
    {},
    this.headers()
  ).pipe(
    catchError(error => {
      console.error(
        'Erreur publication article :',
        error
      );

      return throwError(() => error);
    })
  );
}

}
