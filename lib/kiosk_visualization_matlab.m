systemStatus = csvread('system-status-stream-final-csv.csv',1,1);
kioskIdealCaps = csvread('kiosk-ideal-capacities-csv.csv',1,0);
[m,n] = size(systemStatus);

% ones = ones(m); ones = ones(:,1);
% kioskData = systemStatus(:,3:end);
% kioskIdealCapsMat = ones * kioskIdealCaps;
% fritimestamp = systemStatus(23:31,1);
% frikioskdata = systemStatus(23:31,3:4);
% kioskIdealCapDiffs = kioskData - kioskIdealCapsMat;
% csvwrite('kiosk-ideal-cap-diffs.csv',kioskIdealCapDiffs);


systemStatusDiffs = csvread('kiosk-ideal-cap-diffs.csv',1,1);



