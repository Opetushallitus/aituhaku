// Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
//
// This program is free software:  Licensed under the EUPL, Version 1.1 or - as
// soon as they will be approved by the European Commission - subsequent versions
// of the EUPL (the "Licence");
//
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// European Union Public Licence for more details.

'use strict';

describe('yhteiset.palvelut.debounce', function(){

  var debounce,
      viivastytettava,
      $timeout;

  beforeEach(module('yhteiset.palvelut.debounce'));

  beforeEach(inject(function(_debounce_, _$timeout_) {
    debounce = _debounce_;
    viivastytettava = jasmine.createSpy('Viivästytettävä funktio');
    $timeout = _$timeout_;
  }));

  it('kutsuu viivästytettävää funktioita yhden kerran', function(){
    var d = debounce(viivastytettava, 500);

    d('arvo1');
    d('arvo2');
    d('arvo3');

    $timeout.flush();

    expect(viivastytettava.calls.count()).toEqual(1);
    expect(viivastytettava).toHaveBeenCalledWith('arvo3');
  });
});