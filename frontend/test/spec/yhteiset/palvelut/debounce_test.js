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