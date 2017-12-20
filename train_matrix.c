#include<stdio.h>
int main(){
	int a[500][1001];
	int i,j;
	FILE *fp;
	 
	for(i=0; i<500; i++){
		for(j=0; j<1001; j++){
			scanf("%d",&a[i][j]);
		}
	}
	
	if((fp=fopen("1236.txt","w"))==NULL) {
		printf("File cannot be opened/n");
		exit(1);
	}
	
	else{
		for(i=0; i<500; i++){
			for(j=0; j<1001; j++){
				if(a[i][j]!=0 && j<1000) {
				//	printf("%d:%d ", j-1, a[i][j]);
					fprintf(fp,"%d:%d ", j+1, a[i][j]);
				}
				else if(j==1000) 	fprintf(fp,"%d\n", a[i][j]);
		}
	}

	}
	fclose(fp);
	return 0;
}
