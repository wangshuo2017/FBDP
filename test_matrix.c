#include<stdio.h>
int main(){
	int a[267][1000];
	int i,j;
	FILE *fp;
	 
	for(i=0; i<267; i++){
		for(j=0; j<1000; j++){
			scanf("%d",&a[i][j]);
		}
	}
	
	if((fp=fopen("3500.txt","w"))==NULL) {
		printf("File cannot be opened/n");
		exit(1);
	}
	
	else{
		for(i=0; i<267; i++){
			for(j=0; j<1000; j++){
				if(a[i][j]!=0){
					if(j<999) fprintf(fp,"%d:%d ", j+1, a[i][j]);
					else fprintf(fp, "%d:%d", 1000, a[i][999]);
				}
			}
			fprintf(fp,"\n");
		}
	}
	fclose(fp);
	return 0;
}
