filename = 'system-status-stream-final-csv.csv';
M = csvread(filename,1,0);
[m,n] = size(M);
timestamp = M(:,1);
kioskdata = M(:,2);
plot(timestamp,kioskdata);
disp(timestamp);